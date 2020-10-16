package org.epragati.tickets.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.epragati.exception.BadRequestException;
import org.epragati.ticket.dao.SupportUserCreationDTO;
import org.epragati.ticket.dao.SupportUserDAO;
import org.epragati.ticket.dao.TicketAssignedDTO;
import org.epragati.ticket.dao.TicketDTO;
import org.epragati.ticket.dao.TicketLockedDetails;
import org.epragati.ticket.dao.TicketsDAO;
import org.epragati.tickets.model.LockedTicketsVO;
import org.epragati.tickets.model.SupportUserLimited;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;
import org.epragati.tickets.ticketmapper.ActionEnums;
import org.epragati.tickets.ticketmapper.ModuleEnums;
import org.epragati.tickets.ticketmapper.StatusEnums;
import org.epragati.tickets.ticketmapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SupportReportsImpl implements SupportReportsIntra {

	@Autowired
	TicketsDAO ticketsDAO;

	@Autowired
	SupportUserDAO supportUserDAO;

	@Autowired
	TicketMapper ticketMapper;

	@Override
	public List<TicketVO> SupportTicketsView(String user, String role) {
		int index = 4;
		Boolean status = Boolean.FALSE;
		SupportUserCreationDTO dto = supportUserDAO.findByUserId(user);
		if (ObjectUtils.isEmpty(dto)) {
			throw new BadRequestException("User Not Exist with Id : " + user);
		}
		List<String> modules = dto.getModule();
		List<Integer> districtIds = dto.getDistrictId();
		Optional<TicketDTO> lockedTicket = ticketsDAO
				.findByCurrentIndexAndModuleInAndDistrictIdInAndLockedDetailsUserOrderByCreatedDateAsc(index, modules,
						districtIds, user);

		if (lockedTicket.isPresent()) {
			TicketVO lockedPendingTicket = ticketMapper.convertEntity(lockedTicket.get());
			if (ObjectUtils.isEmpty(lockedPendingTicket)) {
				throw new BadRequestException("Mapping Conversion ");
			}
			List<TicketVO> ticketList = new ArrayList<TicketVO>();
			ticketList.add(lockedPendingTicket);
			return ticketList;
		} else {
			Optional<TicketDTO> pendingTicket = ticketsDAO
					.findByCurrentIndexAndModuleInAndDistrictIdInAndIsTicketLockedOrderByCreatedDateAsc(index, modules,
							districtIds, status);
			if (pendingTicket.isPresent()) {
				this.setLockedDetails(user, role, pendingTicket.get());
				TicketVO pendingTickets = ticketMapper.convertEntity(pendingTicket.get());
				List<TicketVO> listTicket = new ArrayList<TicketVO>();
				listTicket.add(pendingTickets);
				return listTicket;
			} else {
				throw new BadRequestException("No records found ");
			}
		}
	}

	@Override
	public Long SupportTicketsPendingCount(String user, String role) {
		int index = 4;
		SupportUserCreationDTO dto = supportUserDAO.findByUserId(user);
		List<String> modules = dto.getModule();
		List<Integer> districtIds = dto.getDistrictId();
		Long count = ticketsDAO.countByCurrentIndexAndModuleInAndDistrictIdIn(index, modules, districtIds);
		return count;
	}

	public void setLockedDetails(String user, String role, TicketDTO ticketDTO) {
		TicketLockedDetails locked = new TicketLockedDetails();
		locked.setLockedDate(ticketMapper.zonedDateConversion());
		locked.setModule(ticketDTO.getModule());
		locked.setRole(role);
		locked.setUser(user);
		locked.setTicketNo(ticketDTO.getTicketNo());
		ticketDTO.setLockedDetails(locked);
		ticketDTO.setIsTicketLocked(Boolean.TRUE);
		ticketsDAO.save(ticketDTO);
	}

	@Override
	public List<LockedTicketsVO> lockedTicketDetails(int districtId) {
		List<LockedTicketsVO> lockedDetails = new ArrayList<>();
		Boolean status = Boolean.TRUE;
		List<TicketDTO> LockedDTO = ticketsDAO.findByDistrictIdAndIsTicketLocked(districtId, status);
		if (ObjectUtils.isEmpty(LockedDTO)) {
			throw new BadRequestException("No Tickets Are Locked");
		}
		for (TicketDTO dto : LockedDTO) {
			LockedTicketsVO vo = new LockedTicketsVO();
			vo.setUser(dto.getLockedDetails().getUser());
			vo.setModule(dto.getLockedDetails().getModule());
			vo.setRole(dto.getLockedDetails().getRole());
			vo.setTicketNo(dto.getLockedDetails().getTicketNo());
			vo.setLockedDate(dto.getLockedDetails().getLockedDate());
			lockedDetails.add(vo);
		}
		return lockedDetails;
	}

	@Override
	public List<TicketVO> SupportAssignedTicketsView(String user, String role) {
		TicketDTO assignedTickets = ticketsDAO.findByTicketAssignedDTOAssignedToOrderByCreatedDateAsc(user);
		List<TicketDTO> assignedTicketslist=new ArrayList<TicketDTO>();
		assignedTickets.getTicketAssignedDTO().forEach(assign->{
				if(assign.getIsTicketAssigned().equals(Boolean.TRUE)&&assign.getAssignedTo().equals(user)) {
					assignedTicketslist.add(assignedTickets);
				}
			});
		return ticketMapper.convertEntityList(assignedTicketslist);
	}

	@Override
	public List<SupportUserLimited> getAllUsers() {
		List<SupportUserCreationDTO> supportUserList = supportUserDAO.findAllByOrderByUserIdAsc();
		List<SupportUserLimited> limitedDataSupport = new ArrayList<SupportUserLimited>();
		supportUserList.forEach(user -> {
			SupportUserLimited limited = new SupportUserLimited();
			limited.setUserId(user.getUserId());
			limited.setUserName(user.getFirstName()+user.getLastName());
			limitedDataSupport.add(limited);
		});
		return limitedDataSupport;
	}

	@Override
	public List<TicketVO> getSpecialNumberTicketData(String user, String role) {
		List<Integer> index = Arrays.asList(1,2,3,4);
		SupportUserCreationDTO dto = supportUserDAO.findByUserId(user);
		List<Integer> districtIds = dto.getDistrictId();
		String module = ModuleEnums.SPECIALNUMBER.getModules();
		Optional<TicketDTO> specialTicketData = ticketsDAO
				.findByCurrentIndexInAndModuleAndDistrictIdInOrderByCreatedDateAsc(index, module, districtIds);
		if (specialTicketData.isPresent()) {
			this.setLockedDetails(user, role, specialTicketData.get());
			TicketVO specialNumberData = ticketMapper.convertEntity(specialTicketData.get());
			List<TicketVO> SpecialNoVOList = new ArrayList<TicketVO>();
			SpecialNoVOList.add(specialNumberData);
			return SpecialNoVOList;
		} else {
			throw new BadRequestException("No records found ");
		}

	}

	@Override
	public Long specialTicketsPendingCount(String user, String role) {
		List<Integer> index = Arrays.asList(1,2,3,4);
		SupportUserCreationDTO dto = supportUserDAO.findByUserId(user);
		String module = ModuleEnums.SPECIALNUMBER.getModules();
		List<Integer> districtIds = dto.getDistrictId();
		Long count = ticketsDAO.countByCurrentIndexInAndModuleAndDistrictIdIn(index, module, districtIds);
		return count;
	}

	@Override
	public String assignedTicket(UpdateTicketVO assignedTicketVo) {
		TicketDTO updateTicketDTO = ticketsDAO.findByTicketNo(assignedTicketVo.getTicketNo());
		if (ObjectUtils.isEmpty(updateTicketDTO)) {
			throw new BadRequestException("No RECORD FOUND");
		}
		List<TicketAssignedDTO> listAssigned = new ArrayList<TicketAssignedDTO>();
		TicketAssignedDTO assigned = new TicketAssignedDTO();
		assigned.setAssignedBy(assignedTicketVo.getUser());
		assigned.setAssignedTo(assignedTicketVo.getAssignedTo());
		assigned.setAssignedDate(ticketMapper.zonedDateConversion());
		assigned.setAssignComments(assignedTicketVo.getAssignedComment());
		assigned.setIsTicketAssigned(Boolean.TRUE);
		listAssigned.add(assigned);
		if (!CollectionUtils.isEmpty(updateTicketDTO.getTicketAssignedDTO() )) {

			updateTicketDTO.getTicketAssignedDTO().stream().forEach(a -> {
				a.setIsTicketAssigned(Boolean.FALSE);
			});
			listAssigned.addAll(updateTicketDTO.getTicketAssignedDTO());
		}
		updateTicketDTO.setTicketAssignedDTO(listAssigned);
		//listAssigned.clear();
		updateTicketDTO.setStatus(StatusEnums.ASSIGNED.getStatus());
		updateTicketDTO.setCurrentIndex(8);
		updateTicketDTO.setCurrentRole(ActionEnums.SUPPORT.getActionRole());
		updateTicketDTO.setlUpdatedRole(assignedTicketVo.getRole());
		updateTicketDTO.setlUpdatedUser(assignedTicketVo.getUser());
		updateTicketDTO.setlUpdate(ticketMapper.zonedDateConversion());
		updateTicketDTO.setLockedDetails(null);
		updateTicketDTO.setIsTicketLocked(Boolean.FALSE);
		ticketsDAO.save(updateTicketDTO);
		updateTicketDTO.setLockedDetails(null);
		updateTicketDTO.setIsTicketLocked(Boolean.FALSE);
		return "Ticket is assigned to" + " " + assignedTicketVo.getAssignedTo();

	}

	@Override
	public int assignedTicketsCount(String user, String role) {
		List<TicketDTO> ticketDTOList = ticketsDAO.findByTicketAssignedDTOAssignedTo(user);
		List<TicketDTO> assignedTicketslist=new ArrayList<TicketDTO>();
		ticketDTOList.forEach(a->{
			a.getTicketAssignedDTO().forEach(assign->{
				if(assign.getIsTicketAssigned().equals(Boolean.TRUE)&&assign.getAssignedTo().equals(user)) {
					assignedTicketslist.add(a);
				}
			});
		});
		return assignedTicketslist.size();
	}
}
