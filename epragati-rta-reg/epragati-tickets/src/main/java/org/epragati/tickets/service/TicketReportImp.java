package org.epragati.tickets.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.epragati.exception.BadRequestException;
import org.epragati.master.dao.DistrictDAO;
import org.epragati.master.dto.DistrictDTO;
import org.epragati.ticket.dao.SubModulesDAO;
import org.epragati.ticket.dao.TicketDTO;
import org.epragati.ticket.dao.TicketsDAO;
import org.epragati.tickets.model.ClosedView;
import org.epragati.tickets.model.DistrictLevelCount;
import org.epragati.tickets.model.ModuleBaseCount;
import org.epragati.tickets.model.RaisedCount;
import org.epragati.tickets.model.TicketCounts;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;
import org.epragati.tickets.ticketmapper.StatusEnums;
import org.epragati.tickets.ticketmapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TicketReportImp implements TicketReportIntra {

	@Autowired
	TicketsDAO ticketsDAO;

	@Autowired
	TicketMapper ticketMapper;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	DistrictDAO districtDAO;
	
	@Autowired
	SubModulesDAO subModulesDAO;

	@Override
	public TicketVO getTicketData(String ticketNo) {
		TicketDTO ticketDTO = ticketsDAO.findByTicketNo(ticketNo);
		if (ObjectUtils.isEmpty(ticketDTO)) {
			throw new BadRequestException("NO data found" + ticketNo);
		}
		TicketVO ticketvo = ticketMapper.convertEntity(ticketDTO);
		if (ObjectUtils.isEmpty(ticketvo)) {
			throw new BadRequestException("Error ocuured at mapping" + ticketNo);
		}
		return ticketvo;
	}
	@Override
	public List<TicketVO> getTicketDataList(String ticketNo){
		TicketVO ticketVO = this.getTicketData(ticketNo);
		List<TicketVO> listTicket = new ArrayList<TicketVO>();
		listTicket.add(ticketVO);
		return listTicket;
	}

	@Override
	public List<TicketVO> getFirstTicketByOfficeCodeandRole(String officeCode, String currentRole) {
		TicketDTO ticketDetails = ticketsDAO.findFirstByOfficeCodeAndCurrentRoleOrderByCreatedDateAsc(officeCode,
				currentRole);
		List<TicketVO> listTicket = new ArrayList<TicketVO>();
		if (ObjectUtils.isEmpty(ticketDetails)) {
			throw new BadRequestException("No pending found");
		}
		TicketVO ticketVo = this.getTicketData(ticketDetails.getTicketNo());
		listTicket.add(ticketVo);
		return listTicket;
	}

	@Override
	public List<TicketVO> getFirstTicketByOfficeCode(String officeCode, List<Integer> index) {
		TicketDTO ticketDetails = ticketsDAO.findFirstByOfficeCodeAndCurrentIndexInOrderByCreatedDateAsc(officeCode,
				index);
		List<TicketVO> listTicket = new ArrayList<TicketVO>();
		if (StringUtils.isEmpty(ticketDetails.getTicketNo())) {
			throw new BadRequestException("No records found");
		}
		TicketVO ticketVo = this.getTicketData(ticketDetails.getTicketNo());
		listTicket.add(ticketVo);
		return listTicket;
	}

	public TicketCounts adminLevelTicketRaised(String officeCode) {
		TicketCounts tcraised = new TicketCounts();
		Aggregation agg = ticketsDAO.adminLevelRaisedTicketCount(officeCode);
		AggregationResults<RaisedCount> ticketCount = mongoTemplate.aggregate(agg, TicketDTO.class, RaisedCount.class);
		List<RaisedCount> count = ticketCount.getMappedResults();
		count.forEach(a -> {
			if (a.getFirstCreatedRole().equals("CCO")) {
				tcraised.setCcoRaised(a.getCount());
			} else if (a.getFirstCreatedRole().equals("AO")) {
				tcraised.setAoRaised(a.getCount());
			} else if (a.getFirstCreatedRole().equals("RTO")) {
				tcraised.setRtoRaised(a.getCount());
			}
		});
		return tcraised;

	}

	public TicketCounts adminLevelTicketPending(String officeCode) {
		TicketCounts tcpen = new TicketCounts();
		Aggregation agg = ticketsDAO.pendingTicketsAdminLevel(officeCode);
		AggregationResults<RaisedCount> ticketCount = mongoTemplate.aggregate(agg, TicketDTO.class, RaisedCount.class);
		List<RaisedCount> count = ticketCount.getMappedResults();
		count.forEach(a -> {
			if (a.getCurrentIndex() == 1) {
				tcpen.setCcoPending(a.getCount());
			} else if (a.getCurrentIndex() == 2) {
				tcpen.setAoPending(a.getCount());
			} else if (a.getCurrentIndex() == 3) {
				tcpen.setRtoPending(a.getCount());
			}
		});
		return tcpen;

	}

	public TicketCounts adminLevelApproved(String officeCode) {
		TicketCounts tcapp = new TicketCounts();
		List<String> actionEnums = Arrays.asList(StatusEnums.AOAPPROVED.getStatus(),
				StatusEnums.CCOAPPROVED.getStatus(), StatusEnums.RTOAPPROVED.getStatus());
		Aggregation agg = ticketsDAO.adminLevelApprovedTicketCount(officeCode, actionEnums);
		AggregationResults<RaisedCount> ticketCount = mongoTemplate.aggregate(agg, TicketDTO.class, RaisedCount.class);
		List<RaisedCount> count = ticketCount.getMappedResults();
		count.forEach(a -> {
			if (a.getApprovedLevels().equals(StatusEnums.CCOAPPROVED.getStatus())) {
				tcapp.setCcoApproved(a.getCount());
			} else if (a.getApprovedLevels().equals(StatusEnums.AOAPPROVED.getStatus())) {
				tcapp.setAoApproved(a.getCount());
			} else if (a.getApprovedLevels().equals(StatusEnums.RTOAPPROVED.getStatus())) {
				tcapp.setRtoApproved(a.getCount());

			}
		});
		return tcapp;
	}
	public Long ClosedTickets(String officeCode) {
		String actionEnum = StatusEnums.CLOSED.getStatus();
		Long count = ticketsDAO.countByOfficeCodeAndStatus(officeCode, actionEnum);		
	return count;
	}
	public TicketCounts getAllLevelReports(String officeCode) {
		TicketCounts ticketReports = new TicketCounts();
		TicketCounts raised = this.adminLevelTicketRaised(officeCode);
		TicketCounts pending = this.adminLevelTicketPending(officeCode);
		TicketCounts approved = this.adminLevelApproved(officeCode);
		ticketReports.setClosedTickets(this.ClosedTickets(officeCode));
		ticketReports.setCcoRaised(raised.getCcoRaised() != null ? raised.getCcoRaised() : 0);
		ticketReports.setAoRaised(raised.getAoRaised() != null ? raised.getAoRaised() : 0);
		ticketReports.setRtoRaised(raised.getRtoRaised() != null ? raised.getRtoRaised() : 0);
		ticketReports.setCcoPending(pending.getCcoPending() != null ? pending.getCcoPending() : 0);
		ticketReports.setAoPending(pending.getAoPending() != null ? pending.getAoPending() : 0);
		ticketReports.setRtoPending(pending.getRtoPending() != null ? pending.getRtoPending() : 0);
		ticketReports.setCcoApproved(approved.getCcoApproved() != null ? approved.getCcoApproved() : 0);
		ticketReports.setAoApproved(approved.getAoApproved() != null ? approved.getAoApproved() : 0);
		ticketReports.setRtoApproved(approved.getRtoApproved() != null ? approved.getRtoApproved() : 0);
		return ticketReports;

	}

	@Override
	public TicketCounts DealerAndFincierTicketCounts(String officeCode, String user, String role) {
		List<Integer> index = Arrays.asList(1, 2, 3);
		int support = 4;
		int closed = 0;
		TicketCounts ticketCount = new TicketCounts();
		Long raised = ticketsDAO.countByOfficeCodeAndFirstCreatedRoleAndFirstCreatedUser(officeCode, role, user);
		Long rtaPending = ticketsDAO.countByOfficeCodeAndFirstCreatedRoleAndFirstCreatedUserAndCurrentIndexIn(
				officeCode, role, user, index);
		Long supportPending = ticketsDAO.countByOfficeCodeAndFirstCreatedRoleAndFirstCreatedUserAndCurrentIndex(
				officeCode, role, user, support);
		Long closedTickets = ticketsDAO
				.countByOfficeCodeAndFirstCreatedRoleAndFirstCreatedUserAndCurrentIndex(officeCode, role, user, closed);
		ticketCount.setDealerOrFinancierRaised(raised != null ? raised : 0);
		ticketCount.setRtaOfficePending(rtaPending != null ? rtaPending : 0);
		ticketCount.setSupportPending(supportPending != null ? supportPending : 0);
		ticketCount.setClosedTickets(closedTickets != null ? closedTickets : 0);
		return ticketCount;
	}

	@Override
	public List<DistrictLevelCount> getPendingCountInDistrict(Integer index) {
		String module = "DL";
		String status = "Y";
		List<DistrictLevelCount> voList = new ArrayList<DistrictLevelCount>();
		List<DistrictDTO> dto = districtDAO.findByStatus(status);
		List<Integer> districtIds = dto.stream().map(p -> p.getDistrictId()).collect(Collectors.toList());
		for (Integer ids : districtIds) {
			DistrictDTO dtos = districtDAO.findByDistrictIdAndStatus(ids, status);
			Long count = ticketsDAO.countByCurrentIndexAndDistrictId(index, ids);
			Long dlCount = ticketsDAO.countByCurrentIndexAndDistrictIdAndModule(index, ids, module);
			Long regCount = count - dlCount;
			DistrictLevelCount vo = new DistrictLevelCount();
			vo.setDistrictName(dtos.getDistrictName());
			vo.setCount(count);
			vo.setDlcount(dlCount);
			vo.setRegCount(regCount);
			voList.add(vo);
		}
		if (CollectionUtils.isEmpty(voList)) {
			throw new BadRequestException("No Records Found");
		}
		return voList;
	}

	@Override
	public List<ClosedView> getClosedTicketsVO(String officeCode, String status, String userId) {

		List<ClosedView> voList = new ArrayList<ClosedView>();
		List<TicketDTO> listTicketDTO = new ArrayList<TicketDTO>();

		listTicketDTO = StringUtils.isEmpty(userId)
				? ticketsDAO.findByOfficeCodeAndStatus(officeCode, status)
				:  ticketsDAO.findByOfficeCodeAndFirstCreatedUserAndStatus(officeCode, userId, status);
		for (TicketDTO dto : listTicketDTO) {
			ClosedView vo = new ClosedView();
			vo.setTicketNo(dto.getTicketNo());
			vo.setSubModule(dto.getSubModule());
			vo.setIssueType(dto.getIssueType());
			vo.setTicketUser(dto.getFirstCreatedUser());
			vo.setOfficeCode(dto.getOfficeCode());
			vo.setStatus(dto.getStatus());
			vo.setRequest(dto.getRequest());
			vo.setSearchBy(dto.getSearchBy());
			vo.setCreatedDate(dto.getCreatedDate());
			vo.setlUpdatedDate(dto.getlUpdate());
			vo.setClosedBy(dto.getClosedBy());
			vo.setClosedRole(dto.getClosedLevel());
			voList.add(vo);
		}

		if (CollectionUtils.isEmpty(voList)) {
			throw new BadRequestException("No Records Found");
		}
		return voList;
	}
	@Override
	public List<ModuleBaseCount> getSupportClosedTicketBasedOnModule(String status, String user) {
		Aggregation aggreagation=ticketsDAO.closedTicketSupportUser(user, status);
		AggregationResults<ModuleBaseCount> moduleBaseCountagg = mongoTemplate.aggregate(aggreagation, TicketDTO.class, ModuleBaseCount.class);
         List<ModuleBaseCount> moduleBaseCount=moduleBaseCountagg.getMappedResults();       
		return moduleBaseCount;
	}

	@Override
	public List<TicketVO> getCitizenTicketData(UpdateTicketVO updateTicketVO) {
		List<TicketVO> citizenTicketData = new ArrayList<TicketVO>();
		TicketDTO citizenDTO = ticketsDAO.findByTicketNoAndFirstCreatedRole(updateTicketVO.getTicketNo(),
				updateTicketVO.getRole());
		if (ObjectUtils.isEmpty(citizenDTO)) {
			throw new BadRequestException("Ticket was not raised at citizen level : " + updateTicketVO.getTicketNo());
		}
		TicketVO citizenVO = ticketMapper.convertEntity(citizenDTO);
		citizenTicketData.add(citizenVO);
		return citizenTicketData;
	}

}
