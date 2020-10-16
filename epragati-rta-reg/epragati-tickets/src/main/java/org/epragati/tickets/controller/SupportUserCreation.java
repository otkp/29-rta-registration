package org.epragati.tickets.controller;

import java.util.List;

import org.epragati.exception.BadRequestException;
import org.epragati.tickets.model.SupportUserCreationVO;
import org.epragati.tickets.model.SupportUserLimited;
import org.epragati.tickets.model.SupportUserSignUp;
import org.epragati.tickets.service.SaveTicketIntra;
import org.epragati.tickets.service.SupportReportsIntra;
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

@RestController
@CrossOrigin
@RequestMapping(value = "/support")
public class SupportUserCreation {
	private static final Logger logger = LoggerFactory.getLogger(SupportUserCreation.class);
	@Autowired
	SaveTicketIntra saveTicketIntra;

	@Autowired
	SupportReportsIntra supportReportsIntra;

	@PostMapping(value = "/saveSupportUsers")
	public GateWayResponse<?> saveSupportUser(@RequestBody SupportUserCreationVO supVO) {
		logger.info("SupportUserCreation [{}] ", supVO.getUserId());
		if (StringUtils.isEmpty(supVO.getUserId())) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "provide the properData");
		}
		try {
			SupportUserSignUp signup = saveTicketIntra.saveSupportUser(supVO);
			return new GateWayResponse<>(true, HttpStatus.OK, signup);
		} catch (BadRequestException bre) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			logger.debug("Exception [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping(value = "/userSignIn")
	public GateWayResponse<?> getuserSignUp(@RequestBody SupportUserSignUp signup) {
		logger.info("SupportUserSignIn [{}] ", signup.getUserId(), signup.getPassword());
		if (StringUtils.isEmpty(signup.getUserId()) && StringUtils.isEmpty(signup.getPassword())) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "provide userId and password");
		}
		try {
			SupportUserSignUp signin = saveTicketIntra.userSignin(signup.getUserId(), signup.getPassword());
			return new GateWayResponse<>(true, HttpStatus.OK, signin);
		} catch (BadRequestException bre) {
			logger.debug("Debug Level [{}] ", bre);
			logger.error("Exception [{}] ", bre.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@PostMapping(value = "/resetpassword")
	public GateWayResponse<?> usersPasswordReset(@RequestParam String userId, String password, String newpassword,
			String confirmpassword) {

		if (StringUtils.isEmpty(password) && StringUtils.isEmpty(newpassword) && StringUtils.isEmpty(confirmpassword)) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "Enter Credentials");
		}
		logger.info("User Password Reset [{}] ", userId);
		if (!(newpassword.equals(confirmpassword))) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "New Password and Confirm Password should be same");
		}
		try {
			return new GateWayResponse<>(true, HttpStatus.OK,
					saveTicketIntra.resetPassword(userId, password, newpassword, newpassword));
		} catch (BadRequestException bre) {
			logger.debug("Debug Level [{}] ", bre);
			logger.error("Exception [{}] ", bre.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@GetMapping(value = "/getUsers")
	public GateWayResponse<?> getusers() {

		try {
			List<SupportUserLimited> userData = supportReportsIntra.getAllUsers();
			return new GateWayResponse<>(true, HttpStatus.OK, userData);
		} catch (BadRequestException bre) {
			logger.debug("Debug Level [{}] ", bre);
			logger.error("Exception [{}] ", bre.getMessage());
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@PostMapping(value = "/updateSupportUsers")
	public GateWayResponse<?> updateSupportUser(@RequestBody SupportUserCreationVO supVO) {
		if (StringUtils.isEmpty(supVO.getUserId())) {
			return new GateWayResponse<>(HttpStatus.BAD_REQUEST, "provide properData");
		}

		try {
			String result = saveTicketIntra.updateSupportUser(supVO);
			return new GateWayResponse<>(true, HttpStatus.OK, result);
		} catch (BadRequestException bre) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND, bre.getMessage());
		} catch (Exception e) {
			logger.debug("Exception [{}] ", e);
			logger.error("Exception [{}] ", e.getMessage());
			return new GateWayResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

}
