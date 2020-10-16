package org.epragati.tickets.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.epragati.exception.BadRequestException;
import org.epragati.ticket.dao.ApplicationIssueDAO;
import org.epragati.tickets.model.ApplicationIssueVO;
import org.epragati.tickets.model.UpdateIssueVO;
import org.epragati.tickets.service.ApplicationIssueService;
import org.epragati.util.GateWayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/issues")
@CrossOrigin
public class ApplicationIssueController {
	private static final Logger logger = LoggerFactory.getLogger(MasterTicektController.class);
	@Autowired
	ApplicationIssueService applicationIssueService;

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	ApplicationIssueDAO applicationIssueDAO;

	@PostMapping(value = "/saveIssue")
	public GateWayResponse<?> saveIssue(@RequestParam(required = false, value = "issueVO") String applicationIssueVO,
			@RequestParam(required = false, value = "files") MultipartFile[] files)
			throws BadRequestException, IOException {
		logger.info("saveIssue []");
		if (StringUtils.isEmpty(applicationIssueVO)) {
			throw new BadRequestException("Pass All The Required Fields");
		}
		try {
			ApplicationIssueVO vo = objectMapper.readValue(applicationIssueVO, ApplicationIssueVO.class);
			String result = applicationIssueService.saveIssueDetails(vo, files);
			return new GateWayResponse<>(true, HttpStatus.ACCEPTED, result);
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
	@PostMapping(value="/updateIssue")
	public GateWayResponse<?> updateIssue(@RequestBody UpdateIssueVO updateIssueVO){
		logger.info("updateIssue [{}]",updateIssueVO.getUserId());
		if (ObjectUtils.isEmpty(updateIssueVO)) {
			throw new BadRequestException("Pass All The Required Fields");
		}
		try {
			String result = applicationIssueService.updateIssueDetails(updateIssueVO);
			return new GateWayResponse<>(true, HttpStatus.ACCEPTED, result);
		} catch (BadRequestException e) {
			logger.debug("Debug Level [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	@GetMapping(value="/issuesDataList")
	public GateWayResponse<?> getAllIssue(@RequestParam String status,@RequestParam Boolean view){
		logger.info("issuesDataList [{}]");
		if (StringUtils.isEmpty(status)) {
			throw new BadRequestException("Pass All The Required Fields");
		}
		Boolean forCount = Boolean.FALSE;
		try {
			if(forCount.equals(view)) {
				Long count =applicationIssueDAO.countByStatus(status);
				return new GateWayResponse<>(true, HttpStatus.ACCEPTED, count);
			}else {
			List<ApplicationIssueVO> result = applicationIssueService.getListOfIssuesData(status);			
			return new GateWayResponse<>(true, HttpStatus.ACCEPTED, result);
			}
		} catch (BadRequestException e) {
			logger.debug("Debug Level [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	@GetMapping(value="/getIssueData")
	public GateWayResponse<?> getSingleIssueData(@RequestParam String issueNo){
		logger.info("singleIssuesData [{}]",issueNo);
		if (StringUtils.isEmpty(issueNo)) {
			throw new BadRequestException("Pass All The Required Fields");
		}
		try {
			List<ApplicationIssueVO> result = applicationIssueService.getSingleIssueData(issueNo);			
			return new GateWayResponse<>(true, HttpStatus.ACCEPTED, result);
		} catch (BadRequestException e) {
			logger.debug("Debug Level [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, e.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


}
