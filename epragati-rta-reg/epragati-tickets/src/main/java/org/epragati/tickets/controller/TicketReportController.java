package org.epragati.tickets.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.epragati.exception.BadRequestException;
import org.epragati.ticket.dao.TicketsDAO;
import org.epragati.tickets.model.ClosedView;
import org.epragati.tickets.model.DistrictLevelCount;
import org.epragati.tickets.model.LockedTicketsVO;
import org.epragati.tickets.model.ModuleBaseCount;
import org.epragati.tickets.model.TicketCounts;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;
import org.epragati.tickets.service.SaveTicketIntra;
import org.epragati.tickets.service.SupportReportsIntra;
import org.epragati.tickets.service.TicketReportIntra;
import org.epragati.tickets.ticketmapper.StatusEnums;
import org.epragati.util.GateWayResponse;
import org.epragati.util.RequestMappingUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.gridfs.GridFSDBFile;

@CrossOrigin
@RestController
@RequestMapping(RequestMappingUrls.REPORT_TICKETS)
public class TicketReportController {

	private static final Logger logger = LoggerFactory.getLogger(TicketReportController.class);

	@Autowired
	TicketReportIntra ticketReportIntra;

	@Autowired
	TicketsDAO ticketsDAO;

	@Autowired
	SaveTicketIntra saveTicketIntra;

	@Autowired
	SupportReportsIntra supportIntra;

	@PostMapping(value = "/getTicketData")
	public GateWayResponse<?> getTicketInfo(@RequestBody UpdateTicketVO updateTicketVO) {
		logger.info("Getting Ticket Data info [{}]", updateTicketVO);
		if (ObjectUtils.isEmpty(updateTicketVO)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "provide updateTicketVO");
		}
		try {
			List<TicketVO> ticketVO = ticketReportIntra.getTicketDataList(updateTicketVO.getTicketNo());
			return new GateWayResponse<>(true, HttpStatus.OK, ticketVO);
		} catch (BadRequestException bre) {
			logger.debug(" [{}] ", bre);
			logger.error(" [{}] ", bre.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(value = "/ticketsCounts")
	public GateWayResponse<?> getTicketsCountBasedOnOfficeCodeandRoles(@RequestParam String officeCode,
			@RequestParam(value = "role") String currentRole, @RequestParam Boolean view) {

		Boolean isForCount = Boolean.FALSE;
		logger.info("Ticket counts By OfficeCode And CurrentRole [{}}", officeCode, currentRole);
		if (StringUtils.isEmpty(officeCode) || StringUtils.isEmpty(currentRole)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		try {
			if (isForCount.equals(view)) {
				int count = ticketsDAO.findByOfficeCodeAndCurrentRole(officeCode, currentRole).size();
				return new GateWayResponse<>(HttpStatus.OK, count, "Success");
			} else {
				List<TicketVO> vo = ticketReportIntra.getFirstTicketByOfficeCodeandRole(officeCode, currentRole);
				return new GateWayResponse<>(HttpStatus.OK, vo, "Success");
			}

		} catch (BadRequestException bre) {
			logger.debug(" [{}] ", bre);
			logger.error(" [{}] ", bre.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(value = "/ticketReportsCount")
	public GateWayResponse<?> getReportsCount(@RequestParam String officeCode) {
		if (StringUtils.isEmpty(officeCode)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "OfficeCodeNotFound");
		}
		TicketCounts count = ticketReportIntra.getAllLevelReports(officeCode);
		return new GateWayResponse<>(count);
	}

	@GetMapping(value = "/ticketReportsCountDealer")
	public GateWayResponse<?> getReportsCountDealer(@RequestParam String officeCode, @RequestParam String user,
			@RequestParam String role) {
		if (StringUtils.isEmpty(officeCode) && StringUtils.isEmpty(role) && StringUtils.isEmpty(user)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "please provide required fields");
		}
		TicketCounts count = ticketReportIntra.DealerAndFincierTicketCounts(officeCode, user, role);
		return new GateWayResponse<>(count);
	}

	@GetMapping(value = "/getAllLevelticketsCounts")
	public GateWayResponse<?> getAllLevelticketsCountsInRtoLevel(@RequestParam String officeCode,
			@RequestParam(value = "role") String currentRole, @RequestParam Boolean view) {

		Boolean isForCount = Boolean.FALSE;
		List<Integer> index = Arrays.asList(1, 2);
		logger.info("All Tickets Count in RTO[{}}", officeCode, currentRole);
		if (StringUtils.isEmpty(officeCode) || StringUtils.isEmpty(currentRole)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		try {
			if (isForCount.equals(view)) {

				int count = ticketsDAO.countByOfficeCodeAndCurrentIndexIn(officeCode, index);
				return new GateWayResponse<>(HttpStatus.OK, count, "Success");
			} else {
				List<TicketVO> vo = ticketReportIntra.getFirstTicketByOfficeCode(officeCode, index);
				return new GateWayResponse<>(HttpStatus.OK, vo, "Success");
			}
		} catch (BadRequestException bre) {
			logger.debug(" [{}] ", bre);
			logger.error(" [{}] ", bre.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(value = "/districtLevelTicketPending")
	public GateWayResponse<?> districtLevelTicketPending(@RequestParam String role) {
		List<DistrictLevelCount> count = ticketReportIntra.getPendingCountInDistrict(4);
		return new GateWayResponse<>(count);
	}

	@GetMapping(value = "/closedTicketData")
	public GateWayResponse<?> closedTicketsVO(@RequestParam String officeCode, @RequestParam String status,
			@RequestParam(required = false) String userId) {
		logger.info("Closed Ticket Count in Office[{}}", officeCode);
		if (StringUtils.isEmpty(officeCode) || StringUtils.isEmpty(status)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		try {
			List<ClosedView> vo = ticketReportIntra.getClosedTicketsVO(officeCode, status, userId);
			return new GateWayResponse<>(HttpStatus.OK, vo, "success");

		} catch (BadRequestException be) {
			logger.debug(" [{}] ", be);
			logger.error(" [{}] ", be.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, be.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(path = "/getApplicantTicketImageById")
	public void getImageAsByteArrayById(@RequestParam(value = "appImageDocId") String appImageDocId,
			HttpServletResponse response) throws IOException {
		logger.info("Applicant Image Id[{}}", appImageDocId);
		Optional<GridFSDBFile> imageOptional = saveTicketIntra.findingFilesInGridFsById(appImageDocId);
		if (imageOptional.isPresent()) {
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			imageOptional.get().writeTo(response.getOutputStream());
			return;
		}
		throw new RuntimeException("No record found.");
	}

	@GetMapping(path = "/SupportTickets")
	public GateWayResponse<?> SupportTicketsView(@RequestParam String user, @RequestParam String role,
			@RequestParam Boolean view) {
		logger.info("Support Tickets [{}}", user);
		if (StringUtils.isEmpty(role) || StringUtils.isEmpty(user)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		Boolean check = Boolean.FALSE;
		try {
			if (check.equals(view)) {
				Long count = supportIntra.SupportTicketsPendingCount(user, role);
				return new GateWayResponse<>(true, HttpStatus.OK, count);
			} else {
				List<TicketVO> vo = supportIntra.SupportTicketsView(user, role);
				return new GateWayResponse<>(true, HttpStatus.OK, vo);
			}
		} catch (BadRequestException be) {
			logger.debug(" [{}] ", be);
			logger.error(" [{}] ", be.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, be.getMessage());
		} catch (NullPointerException ne) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ne.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(path = "/LockedDetails")
	public GateWayResponse<?> LockedTicketsView(@RequestParam Integer districtId, @RequestParam Boolean view) {
		logger.info("LockedTickets View [{}}", districtId);
		if (districtId == null) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Id is Missing");
		}
		Boolean check = Boolean.FALSE;
		try {
			if (check.equals(view)) {
				Boolean status = Boolean.TRUE;
				Long count = ticketsDAO.countByDistrictIdAndIsTicketLocked(districtId, status);
				return new GateWayResponse<>(true, HttpStatus.OK, count);
			} else {
				List<LockedTicketsVO> vo = supportIntra.lockedTicketDetails(districtId);
				return new GateWayResponse<>(true, HttpStatus.OK, vo);
			}

		} catch (BadRequestException be) {
			logger.debug(" [{}] ", be);
			logger.error(" [{}] ", be.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, be.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@GetMapping(value = "/moduleLevelClosedCount")
	public GateWayResponse<?> moduleLevelClosedCount(@RequestParam String user) {
		logger.info("Module Count [{}}", user);
		if (user == null) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "user is Missing");
		}
		List<ModuleBaseCount> count = ticketReportIntra
				.getSupportClosedTicketBasedOnModule(StatusEnums.CLOSED.getStatus(), user);
		return new GateWayResponse<>(true, HttpStatus.OK, count);
	}
	@GetMapping(value ="/assignedTickets")
	public GateWayResponse<?> AssignedTickets(@RequestParam String user, @RequestParam String role,@RequestParam Boolean view){
		logger.info("Assigned Tickets [{}] ", user);
		if (StringUtils.isEmpty(role) || StringUtils.isEmpty(user)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		Boolean check = Boolean.FALSE;
		try {
			if(check.equals(view)) {			
			return new GateWayResponse<>(true, HttpStatus.OK, supportIntra.assignedTicketsCount(user,role));
			}else {
				List<TicketVO> vo =supportIntra.SupportAssignedTicketsView(user, role);
				return new GateWayResponse<>(true, HttpStatus.OK, vo);
			}
			
		}catch (BadRequestException be) {
			logger.debug(" [{}] ", be);
			logger.error(" [{}] ", be.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, be.getMessage());
		} catch (NullPointerException ne) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ne.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@PostMapping(value = "/citizenTicketData")
	public GateWayResponse<?> citizenTicketData(@RequestBody UpdateTicketVO updateTicketVO) {
		logger.info("CitizenTicketData [{}] ", updateTicketVO.getTicketNo());
		if (StringUtils.isEmpty(updateTicketVO.getTicketNo()) || StringUtils.isEmpty(updateTicketVO.getRole())) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		try {
			List<TicketVO> vo = ticketReportIntra.getCitizenTicketData(updateTicketVO);
			return new GateWayResponse<>(true, HttpStatus.OK, vo);
		} catch (BadRequestException be) {
			logger.debug(" [{}] ", be);
			logger.error(" [{}] ", be.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, be.getMessage());
		} catch (NullPointerException ne) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ne.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping(value = "/specialNumberData")
	public GateWayResponse<?> getSpecialNumberTicketData(@RequestParam String user, @RequestParam String role,
			@RequestParam Boolean view) {
		logger.info("specialNumberData [{}] ");
		if (StringUtils.isEmpty(user) || StringUtils.isEmpty(role)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Required Params are Missing");
		}
		Boolean check = Boolean.FALSE;
		try {
			if (check.equals(view)) {
				Long count = supportIntra.specialTicketsPendingCount(user, role);
				return new GateWayResponse<>(true, HttpStatus.OK, count);
			} else {
				List<TicketVO> voList = supportIntra.getSpecialNumberTicketData(user, role);
				return new GateWayResponse<>(true, HttpStatus.OK, voList);
			}

		} catch (BadRequestException be) {
			logger.debug(" [{}] ", be);
			logger.error(" [{}] ", be.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, be.getMessage());
		} catch (NullPointerException ne) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, ne.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}
