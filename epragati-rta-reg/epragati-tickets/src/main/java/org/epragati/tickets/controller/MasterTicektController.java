package org.epragati.tickets.controller;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.epragati.constants.MessageKeys;
import org.epragati.exception.BadRequestException;
import org.epragati.master.dao.DistrictDAO;
import org.epragati.master.dao.MandalDAO;
import org.epragati.master.dao.OfficeDAO;
import org.epragati.master.dto.DistrictDTO;
import org.epragati.master.dto.MandalDTO;
import org.epragati.master.dto.OfficeDTO;
import org.epragati.ticket.dao.IssueTypesDTO;
import org.epragati.ticket.dao.ModuleDAO;
import org.epragati.ticket.dao.ModuleDTO;
import org.epragati.ticket.dao.SubModulesDAO;
import org.epragati.ticket.dao.SubModulesDTO;
import org.epragati.ticket.dao.TicketTypesDAO;
import org.epragati.util.GateWayResponse;
import org.epragati.util.RequestMappingUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RequestMappingUrls.MASTER_TICKETS)
@CrossOrigin
public class MasterTicektController {
	private static final Logger logger = LoggerFactory.getLogger(MasterTicektController.class);

	@Autowired
	DistrictDAO districtDAO;
	
	@Autowired
	MandalDAO mandalDAO;

	@Autowired
	OfficeDAO officeDAO;

	@Autowired
	ModuleDAO moduleDAO;

	@Autowired
	TicketTypesDAO ticketTypesDAO;

	@Autowired
	SubModulesDAO subModulesDAO;

	@GetMapping(path = "/getOfficeNameByDistrict", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> getOfficeByDistrict(
			@RequestParam(value = "districtId", required = true) Integer districtId) {
		logger.info("districtId In Controller [{}}", districtId);
		try {

			if (districtId == null) {
				return new GateWayResponse<String>(HttpStatus.BAD_REQUEST, "Required District.");
			}
			List<OfficeDTO> result = officeDAO.findByDistrict(districtId);
			return new GateWayResponse<>(HttpStatus.OK, result, "Success");
		} catch (BadRequestException e) {
			logger.debug(" [{}] ", e);
			logger.error(" [{}] ", e.getMessage());
			return new GateWayResponse<String>(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (NullPointerException e) {
			return new GateWayResponse<String>(HttpStatus.BAD_REQUEST,
					MessageKeys.CITIZEN_SERVICE_NULLPOINTER_EXCEPTION);
		} catch (Exception e) {
			return new GateWayResponse<String>(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
		}

	}

	@GetMapping(value = "/getAllDisrictsNames", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> getAllDistricts(String stateId) {
		logger.info("allDistricts");
		try {
			List<DistrictDTO> districtsList = districtDAO.findByStateId(stateId);
			return new GateWayResponse<>(true, HttpStatus.OK, districtsList);
		} catch (Exception e) {
			logger.debug("Exception [{}]", e);
			logger.error("Exception [{}]", e.getMessage());
			return new GateWayResponse<String>(true, HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
		}

	}
	@GetMapping(value ="/getMandals", produces = { MediaType.APPLICATION_JSON_VALUE })
	public GateWayResponse<?> getAllMandals(@RequestParam(value = "districtId", required = true) Integer districtId){
		logger.info("Mandals List " ,districtId);
		if (districtId == null) {
			return new GateWayResponse<String>(HttpStatus.BAD_REQUEST, "Required District.");
		}
		try {
			List<MandalDTO> mandalDTO=mandalDAO.findByDistrictId(districtId);
			if(ObjectUtils.isEmpty(mandalDTO)) {
				return new GateWayResponse<String>(true,HttpStatus.NOT_FOUND, "No Records Found");
			}
			return new GateWayResponse<>(true, HttpStatus.OK, mandalDTO);
		}catch (Exception e) {
			logger.debug(" [{}] ", e);
			logger.error(" [{}] ", e.getMessage());
			return new GateWayResponse<String>(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
		}
		
	}
	

	@GetMapping(value = "/getCitizenModules", produces = MediaType.APPLICATION_JSON_VALUE)
	public GateWayResponse<?> getCitizenModules() {
		logger.info("allCitizenModules");
		ModuleDTO moduleDTO = moduleDAO.findByIsEnableTrue();
		if (ObjectUtils.isEmpty(moduleDTO)) {
		 return new GateWayResponse<String>(HttpStatus.BAD_REQUEST, "Data Not Found");
		}
		return new GateWayResponse<>(true, HttpStatus.OK, moduleDTO);
	}

	@GetMapping(value = "/getCitizenSubModules", produces = MediaType.APPLICATION_JSON_VALUE)
	public GateWayResponse<?> getCitizenSubModules(String module) {
		logger.info("allCitizenSubModules",module);
		if (StringUtils.isEmpty(module)) {
			return new GateWayResponse<String>(HttpStatus.BAD_REQUEST,"provide module type");
		}
		SubModulesDTO subModulesDTO = subModulesDAO.findByticketModule(module);
		if (ObjectUtils.isEmpty(subModulesDTO)) {
			return new GateWayResponse<>(HttpStatus.NOT_FOUND);
		}
		return new GateWayResponse<>(true,HttpStatus.OK,subModulesDTO);
	}

	@GetMapping(value = "/getCitizenIssueTypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public GateWayResponse<?> getCitizenTicketTypes() {
		logger.info("CitizenIssueTypes");
		IssueTypesDTO issueTypesDTO = ticketTypesDAO.findByIsEnableTrue();
		return new GateWayResponse<>(true,HttpStatus.OK,issueTypesDTO);
	}

	// Admin Level Controllers
	@GetMapping(value = "/modules", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getModules() {
		logger.info("Admin Modules");
		ModuleDTO moduleDTO = moduleDAO.findByIsEnableTrue();
		// check enable should have a boolean value true
		if (ObjectUtils.isEmpty(moduleDTO)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(moduleDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/subModules", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSubModules(String module) {
		logger.info("Admin Sub Modules [{}]",module);
		if (StringUtils.isEmpty(module)) {
			return new ResponseEntity<String>("provide module type", HttpStatus.BAD_REQUEST);
		}

		SubModulesDTO subModulesDTO = subModulesDAO.findByticketModule(module);
		if (ObjectUtils.isEmpty(subModulesDTO)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(subModulesDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/issueTypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTicketTypes() {
		logger.info("Admin issue Types [{}]");
		IssueTypesDTO issueTypesDTO = ticketTypesDAO.findByIsEnableTrue();
		return new ResponseEntity<>(issueTypesDTO, HttpStatus.OK);
	}

}
