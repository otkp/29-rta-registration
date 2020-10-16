package org.epragati.tickets.controller;

import java.io.IOException;

import org.epragati.exception.BadRequestException;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;
import org.epragati.tickets.service.SaveTicketIntra;
import org.epragati.tickets.service.SupportReportsIntra;
import org.epragati.tickets.ticketmapper.StatusEnums;
import org.epragati.util.GateWayResponse;
import org.epragati.util.RequestMappingUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
@RequestMapping(RequestMappingUrls.TICKET_GENERATION)
public class TicketController {
	private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	SaveTicketIntra saveTicketIntra;
	
	@Autowired
	SupportReportsIntra supportIntra;

	@PostMapping("/saveAdminTicket")
	public GateWayResponse<?> saveAdminLevelTicketData(
			@RequestParam(required = true, value = "adminTicket") String ticketVo,
			@RequestParam(required = false, value = "files") MultipartFile[] files) {
		logger.info("Save Admin Ticket Data [{}} ", ticketVo);
		if (StringUtils.isEmpty(ticketVo)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "please provide required fields : " + ticketVo);
		}
		try {
			TicketVO ticketVO = objectMapper.readValue(ticketVo, TicketVO.class);
			String ticketNo = saveTicketIntra.saveTicket(ticketVO, files);
			return new GateWayResponse<>(HttpStatus.OK, ticketNo, "Success");
		} catch (IOException e) {
			logger.debug("Debug Level [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.CONFLICT, e.getMessage());
		} catch (BadRequestException bre) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@PostMapping("/updateAdminTicket")
	public GateWayResponse<?> updateTicketData(@RequestBody UpdateTicketVO ticketVO,
			@RequestParam(required = false, value = "files") MultipartFile[] files) {
		logger.info("Update Admin Ticket Data [{}} ", ticketVO);
		if (StringUtils.isEmpty(ticketVO.getTicketNo())) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST,
					"please provide required fields : " + ticketVO.getTicketNo());
		}
		try {
			if (ticketVO.getStatus().equals(StatusEnums.CANCELLED.getStatus())
					|| ticketVO.getStatus().equals(StatusEnums.CLOSED.getStatus())) {
				String status = saveTicketIntra.cancelTicket(ticketVO);
				return new GateWayResponse<>(HttpStatus.OK, status, "Success");
			} else if (ticketVO.getStatus().equals(StatusEnums.ASSIGNED.getStatus())) {
				String status = supportIntra.assignedTicket(ticketVO);
				return new GateWayResponse<>(HttpStatus.OK, status, "Success");
			} else {
				String status = saveTicketIntra.updateTicket(ticketVO, files, ticketVO.getTicketNo());
				return new GateWayResponse<>(HttpStatus.OK, status, "Success");
			}
		} catch (IOException ie) {
			logger.debug("Debug Level [{}] ", ie);
			logger.error("Exception [{}] ", ie.getMessage());
			return new GateWayResponse<>(HttpStatus.CONFLICT, ie.getMessage());
		} catch (BadRequestException bre) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping("/saveCitizenTicket")
	public GateWayResponse<?> saveCitizenTicketData(@RequestParam(required = true) String ticketVO,
			@RequestParam(required = false, value = "files") MultipartFile[] files) {
		logger.info("Save Citizen Ticket Data [{}} ", ticketVO);
		if (StringUtils.isEmpty(ticketVO)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST,
					"please provide all the required fields : " + ticketVO);
		}
		try {
			TicketVO vo = objectMapper.readValue(ticketVO, TicketVO.class);
			String ticketNo = saveTicketIntra.saveCitizenTicket(vo, files);
			return new GateWayResponse<>(HttpStatus.OK, ticketNo, "Success");
		} catch (IOException e) {
			logger.debug("Debug Level [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.CONFLICT, e.getMessage());
		} catch (BadRequestException bre) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping("/updateCitizenTicket")
	public GateWayResponse<?> updateCitzenData(String ticketVO,
			@RequestParam(required = false, value = "files") MultipartFile[] files) {
		logger.info("Update Citizen Ticket Data [{}} ", ticketVO);
		if (StringUtils.isEmpty(ticketVO)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST,
					"please provide all the required fields : " + ticketVO);
		}
		try {
			UpdateTicketVO citizenTicketVO = objectMapper.readValue(ticketVO, UpdateTicketVO.class);
			String status = saveTicketIntra.updateTicket(citizenTicketVO, files, citizenTicketVO.getTicketNo());
			return new GateWayResponse<>(HttpStatus.OK, status, status);
		} catch (IOException ie) {
			logger.debug("Debug Level [{}] ", ie);
			logger.error("Exception [{}] ", ie.getMessage());
			return new GateWayResponse<>(HttpStatus.CONFLICT, ie.getMessage());
		} catch (BadRequestException bre) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}