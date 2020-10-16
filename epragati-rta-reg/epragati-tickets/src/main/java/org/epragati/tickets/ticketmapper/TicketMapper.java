package org.epragati.tickets.ticketmapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.epragati.common.mappers.BaseMapper;
import org.epragati.ticket.dao.ReopenActionRoles;
import org.epragati.ticket.dao.SuggestionActionRolesDTO;
import org.epragati.ticket.dao.TicketDTO;
import org.epragati.ticket.dao.TicketImageDTO;
import org.epragati.ticket.dao.TicketSequnceDAO;
import org.epragati.ticket.dao.TicketSequnceDTO;
import org.epragati.ticket.dao.UpdateActionRoles;
import org.epragati.tickets.model.ReopenActionDetailsVO;
import org.epragati.tickets.model.SuggestionActionRoles;
import org.epragati.tickets.model.TicketImageVO;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateActionRolesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper extends BaseMapper<TicketDTO, TicketVO> {

	@Autowired
	TicketSequnceDAO ticketSequnceDAO;

	@Autowired
	GridFsOperations operations;

	@Autowired
	UpdateIndexing updateIndexing;

	@Value("${ticket.imgae.url:}")
	private String imagePreUrl;

	@Value("${ticket}")
	private String ticket;

	@Override
	public TicketVO convertEntity(TicketDTO dto) {
		TicketVO vo = new TicketVO();
		// BeanUtils.copyProperties(dto, vo);
		funPoint(dto.getModule(), vo::setModule);
		funPoint(dto.getSubModule(), vo::setSubModule);
		funPoint(dto.getIssueType(), vo::setIssueType);
		funPoint(dto.getProblemLevel(), vo::setProblemLevel);
		funPoint(dto.getProblemOcurredAt(), vo::setProblemOcurredAt);
		funPoint(dto.getTicketNo(), vo::setTicketNo);
		funPoint(dto.getTicketUser(), vo::setTicketUser);
		funPoint(dto.getMobileNo(), vo::setMobileNo);
		funPoint(dto.getDistrictId(), vo::setDistrictId);
		funPoint(dto.getDistrictName(), vo::setDistrictName);
		funPoint(dto.getOfficeCode(), vo::setOfficeCode);
		funPoint(dto.getOfficeName(), vo::setOfficeName);
		funPoint(dto.getStatus(), vo::setStatus);
		funPoint(dto.getTrNo(), vo::setTrNo);
		funPoint(dto.getPrNo(), vo::setPrNo);
		funPoint(dto.getCurrentRole(), vo::setCurrentRole);
		funPoint(dto.getRegapplicationNo(), vo::setRegapplicationNo);
		funPoint(dto.getChassisNumber(), vo::setChassisNumber);
		funPoint(dto.getEngineNumber(), vo::setEngineNumber);
		funPoint(dto.getDlNo(), vo::setDlNo);
		funPoint(dto.getDlApplicationNo(), vo::setDlApplicationNo);
		funPoint(dto.getDlAadharNo(), vo::setDlAadharNo);
		funPoint(dto.getClosedBy(), vo::setClosedBy);
		funPoint(dto.getClosedDate(), vo::setClosedDate);
		funPoint(dto.getCreatedDate(), vo::setCreatedDate);
		funPoint(dto.getClosedLevel(), vo::setClosedLevel);
		funPoint(dto.getRequest(), vo::setRequest);
		funPoint(dto.getSearchBy(), vo::setSearchBy);
		funPoint(dto.getlUpdate(), vo::setlUpdate);
		funPoint(dto.getlUpdatedRole(), vo::setlUpdatedRole);
		funPoint(dto.getlUpdatedUser(), vo::setlUpdatedUser);
		funPoint(dto.getFirstCreatedRole(), vo::setFirstCreatedRole);
		funPoint(dto.getFirstCreatedUser(), vo::setFirstCreatedUser);
		funPoint(dto.getIsFromDept(), vo::setIsFromDept);
		// vo.setProblemDesc(dto.getProblemDesc());
		vo.setUpDateActionRoles(this.settingOfupdationActionRoles(dto.getUpdateActionRoles()));
		if (dto.getReopenActionRoles() != null) {
			vo.setReopenActionDetailsVO(this.settingReopenActionDetails(dto.getReopenActionRoles()));
		}
		if (dto.getSuggestionActionRoles() != null) {
			vo.setSuggestionActionRoles(this.gettingSuggestionActionRoles(dto.getSuggestionActionRoles()));
		}
		if (dto.getTicketImageDTO() != null) {
			List<TicketImageDTO> imgDtoList = dto.getTicketImageDTO();
			List<TicketImageVO> imgVoList = new ArrayList<>();
			for (TicketImageDTO imgdto : imgDtoList) {
				TicketImageVO imgVO = new TicketImageVO();
				imgVO.setImageId(imgdto.getImageId());
				imgVO.setImgaeType(imgdto.getImgaeType());
				imgVO.setImagefileName(imgdto.getImagefileName());
				imgVO.setImageurl(imagePreUrl + "?appImageDocId=" + imgdto.getImageId());
				imgVoList.add(imgVO);
			}
			vo.setTicketImageVO(imgVoList);
		}
		return vo;
	}

	public TicketDTO convertVO(TicketVO ticketVo) {
		TicketDTO ticketDTO = new TicketDTO();
		if (ticketVo.getStatus().equals(StatusEnums.NEW.getStatus())
				|| ticketVo.getStatus().equals(StatusEnums.CCOAPPROVED.getStatus())
				|| ticketVo.getStatus().equals(StatusEnums.AOAPPROVED.getStatus())
				|| ticketVo.getStatus().equals(StatusEnums.RTOAPPROVED.getStatus())
				||ticketVo.getStatus().equals(StatusEnums.MVIAPPROVED.getStatus())
				||ticketVo.getStatus().equals(StatusEnums.DTCAPPROVED.getStatus())){

			ticketDTO = updateIndexing.updationOfCurrentIndex(ticketVo.getCreatedRole(),
					ticketVo.getStatus().toUpperCase());
		}
		// BeanUtils.copyProperties(ticketVo, ticketDTO);
		funPoint(ticketVo.getModule(), ticketDTO::setModule);
		funPoint(ticketVo.getSearchBy(), ticketDTO::setSearchBy);
		funPoint(ticketVo.getSubModule(), ticketDTO::setSubModule);
		funPoint(ticketVo.getIssueType(), ticketDTO::setIssueType);
		funPoint(ticketVo.getTicketUser(), ticketDTO::setTicketUser);
		funPoint(ticketVo.getMobileNo(), ticketDTO::setMobileNo);
		funPoint(ticketVo.getProblemOcurredAt(), ticketDTO::setProblemOcurredAt);
		funPoint(ticketVo.getProblemLevel(), ticketDTO::setProblemLevel);
		funPoint(ticketVo.getStatus(), ticketDTO::setStatus);
		funPoint(ticketVo.getOfficeCode(), ticketDTO::setOfficeCode);
		funPoint(ticketVo.getTrNo(), ticketDTO::setTrNo);
		funPoint(ticketVo.getPrNo(), ticketDTO::setPrNo);
		funPoint(ticketVo.getRegapplicationNo(), ticketDTO::setRegapplicationNo);
		funPoint(ticketVo.getChassisNumber(), ticketDTO::setChassisNumber);
		funPoint(ticketVo.getEngineNumber(), ticketDTO::setEngineNumber);
		funPoint(ticketVo.getDlNo(), ticketDTO::setDlNo);
		funPoint(ticketVo.getLlrNo(), ticketDTO::setLlrNo);
		funPoint(ticketVo.getDlAadharNo(), ticketDTO::setDlAadharNo);
		funPoint(ticketVo.getCreatedRole(), ticketDTO::setlUpdatedRole);
		funPoint(ticketVo.getTicketUser(), ticketDTO::setlUpdatedUser);
		funPoint(ticketVo.getDlApplicationNo(), ticketDTO::setDlApplicationNo);
		funPoint(this.ticketNumberGenration(ticket), ticketDTO::setTicketNo);
		funPoint(ticketVo.getRequest(), ticketDTO::setRequest);
		funPoint("AP", ticketDTO::setStateId);
		/*
		 * funPoint(this.saveSolution(ticketVo.getCreatedRole().toUpperCase(),
		 * ticketVo.getProbDesc()), ticketDTO::setProblemDesc);
		 */
		funPoint(
				this.saveActionRoles(ticketVo.getCreatedRole().toUpperCase(), ticketDTO.getTicketNo(),
						ticketVo.getModule(), ticketVo.getTicketUser(), ticketVo.getStatus(), ticketVo.getProbDesc()),
				ticketDTO::setUpdateActionRoles);
		return ticketDTO;
	}

	public List<Map<String, String>> saveSolution(String user, String solution) {
		List<Map<String, String>> listSolution = new ArrayList<Map<String, String>>();
		Map<String, String> solutionMap = new HashMap<String, String>();
		solutionMap.put(user, solution);
		listSolution.add(solutionMap);
		return listSolution;

	}

	// forUpdationProblemDesc
	public List<Map<String, String>> updateSolution(String user, String solution,
			List<Map<String, String>> problmDesc) {
		List<Map<String, String>> listSolution = new ArrayList<Map<String, String>>();
		Map<String, String> solutionMap = new HashMap<String, String>();
		solutionMap.put(user, solution);
		listSolution.add(solutionMap);
		listSolution.addAll(problmDesc);
		return listSolution;

	}

	// forRemarksUpdate
	/*
	 * public List<Map<String, String>> updateRemarks(String user, String comments,
	 * List<Map<String, String>> oldRemarks) {
	 * 
	 * List<Map<String, String>> listSolution = new ArrayList<Map<String,
	 * String>>(); Map<String, String> solutionMap = new HashMap<String, String>();
	 * solutionMap.put(user, comments); listSolution.add(solutionMap); if
	 * (oldRemarks != null) { listSolution.addAll(oldRemarks); } return
	 * listSolution; }
	 */

	// generateAdminTicketData
	public String ticketNumberGenration(String ticket) {
		TicketSequnceDTO ticketSequence = ticketSequnceDAO.findByid(ticket);
		LocalDateTime localDateTime = LocalDateTime.now();
		if(localDateTime.getMonthValue()==ticketSequence.getMonthOfTheYear()) {
			StringBuilder sequence = new StringBuilder();
			Long ticketSeq = ticketSequence.getPresentNumber();
			Long increticketSeq=++ticketSeq;
			sequence.append(String.valueOf(localDateTime.getYear()).substring(2, 4)).
			append(String.valueOf(localDateTime.getMonthValue()))
					.append(String.valueOf(increticketSeq));
			ticketSequence.setPresentNumber(increticketSeq);
			ticketSequnceDAO.save(ticketSequence);
			return sequence.toString();
			
		}else {
			StringBuilder sequence = new StringBuilder();
			Long ticketSeq = ticketSequence.getStartingNumber();
			Long increticketSeq=++ticketSeq;
			sequence.append(String.valueOf(localDateTime.getYear()).substring(2, 4)).
			append(String.valueOf(localDateTime.getMonthValue()))
					.append(String.valueOf(increticketSeq));
			ticketSequence.setPresentNumber(increticketSeq);
			ticketSequence.setMonthOfTheYear(localDateTime.getMonthValue());
			ticketSequnceDAO.save(ticketSequence);
			return sequence.toString();
		}	
	}

	public List<UpdateActionRoles> saveActionRoles(String role, String ticket, String module, String userId,
			String status, String problemDesc) {
		List<UpdateActionRoles> updateActionsList = new ArrayList<UpdateActionRoles>();
		UpdateActionRoles updateActions = new UpdateActionRoles();
		updateActions.setActionStatus(status);
		updateActions.setCreateLocalDate(zonedDateConversion());
		updateActions.setTicket(ticket);
		updateActions.setUserId(userId);
		updateActions.setRole(role);
		updateActions.setModuletype(module);
		updateActions.setComment(problemDesc);
		updateActionsList.add(updateActions);
		return updateActionsList;
	}

	public List<UpdateActionRoles> updateActionRoles(String role, String ticket, String module, String userId,
			String status, String comment, List<UpdateActionRoles> oldactions) {
		List<UpdateActionRoles> updateActionsList = new ArrayList<UpdateActionRoles>();
		UpdateActionRoles updateActions = new UpdateActionRoles();
		funPoint(status, updateActions::setActionStatus);
		funPoint(zonedDateConversion(), updateActions::setCreateLocalDate);
		funPoint(ticket, updateActions::setTicket);
		funPoint(userId, updateActions::setUserId);
		funPoint(role, updateActions::setRole);
		funPoint(module, updateActions::setModuletype);
		funPoint(comment, updateActions::setComment);
		updateActionsList.add(updateActions);
		if (oldactions != null) {
			updateActionsList.addAll(oldactions);
		}
		return updateActionsList;
	}

	// Saving Reopen Comments
	public List<ReopenActionRoles> reopenActionRoles(String role, String userId, String comments,
			List<ReopenActionRoles> oldDetails) {
		List<ReopenActionRoles> reOpenActionDetailsList = new ArrayList<ReopenActionRoles>();
		ReopenActionRoles reopenDetails = new ReopenActionRoles();
		funPoint(userId, reopenDetails::setReOpenBy);
		funPoint(role, reopenDetails::setReOpenRole);
		funPoint(zonedDateConversion(), reopenDetails::setReOpenDate);
		funPoint(comments, reopenDetails::setReOpenComments);
		reopenDetails.setReOpenComments(comments);
		reOpenActionDetailsList.add(reopenDetails);
		if (oldDetails != null) {
			reOpenActionDetailsList.addAll(oldDetails);
		}
		return reOpenActionDetailsList;
	}

	// Retrieving Action Details
	public List<UpdateActionRolesVO> settingOfupdationActionRoles(List<UpdateActionRoles> rolesDto) {
		List<UpdateActionRolesVO> updateActionRolesVo = new ArrayList<UpdateActionRolesVO>();
		rolesDto.forEach(a -> {
			UpdateActionRolesVO vo = new UpdateActionRolesVO();
			funPoint(a.getTicket(), vo::setTicket);
			funPoint(a.getCreateLocalDate(), vo::setCreateLocalDateTime);
			funPoint(a.getRole(), vo::setRole);
			funPoint(a.getComment(), vo::setComment);
			funPoint(a.getUserId(), vo::setUserId);
			funPoint(a.getActionStatus(), vo::setStatus);
			updateActionRolesVo.add(vo);
		});
		return updateActionRolesVo;
	}

	// Retrieving Reopen Comments
	public List<ReopenActionDetailsVO> settingReopenActionDetails(List<ReopenActionRoles> dto) {
		List<ReopenActionDetailsVO> voList = new ArrayList<ReopenActionDetailsVO>();
		dto.forEach(a -> {
			ReopenActionDetailsVO vo = new ReopenActionDetailsVO();
			funPoint(a.getReOpenBy(), vo::setReOpenBy);
			funPoint(a.getReOpenComments(), vo::setReOpenComments);
			funPoint(a.getReOpenDate(), vo::setReOpenDate);
			funPoint(a.getReOpenRole(), vo::setReOpenRole);
			voList.add(vo);
		});
		return voList;
	}

	// zonedDateConversion
	public LocalDateTime zonedDateConversion() {
		ZoneId zoneId = ZoneId.of("Asia/Kolkata");
		return LocalDateTime.now(zoneId);
	}

	// Save Support Suggestions
	public List<SuggestionActionRolesDTO> suggestionActionRoles(String role, String user, String suggestion,
			List<SuggestionActionRolesDTO> oldRecords) {
		List<SuggestionActionRolesDTO> dtoList = new ArrayList<SuggestionActionRolesDTO>();
		SuggestionActionRolesDTO dto = new SuggestionActionRolesDTO();
		funPoint(role, dto::setRole);
		funPoint(user, dto::setUser);
		funPoint(suggestion, dto::setSuggestion);
		funPoint(zonedDateConversion(), dto::setSuggestedDate);
		dtoList.add(dto);
		if (oldRecords != null) {
			dtoList.addAll(oldRecords);
		}
		return dtoList;
	}

	// Retrieving Support Suggestions
	public List<SuggestionActionRoles> gettingSuggestionActionRoles(List<SuggestionActionRolesDTO> dtosList) {
		List<SuggestionActionRoles> voList = new ArrayList<SuggestionActionRoles>();
		dtosList.forEach(a -> {
			SuggestionActionRoles vo = new SuggestionActionRoles();
			funPoint(a.getRole(), vo::setRole);
			funPoint(a.getUser(), vo::setUser);
			funPoint(a.getSuggestion(), vo::setSuggestion);
			funPoint(a.getSuggestedDate(), vo::setSuggestedDate);
			voList.add(vo);
		});
		return voList;
	}
	
	public List<TicketVO> convertEntityList(List<TicketDTO> dtoList){
		return dtoList.stream().map(m->convertEntity(m)).collect(Collectors.toList());
}
}