package org.epragati.tickets.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.epragati.exception.BadRequestException;
import org.epragati.master.dao.DistrictDAO;
import org.epragati.master.dao.MandalDAO;
import org.epragati.master.dao.OfficeDAO;
import org.epragati.master.dto.DistrictDTO;
import org.epragati.master.dto.MandalDTO;
import org.epragati.master.dto.OfficeDTO;
import org.epragati.ticket.dao.SupportUserCreationDTO;
import org.epragati.ticket.dao.SupportUserDAO;
import org.epragati.ticket.dao.TicketDTO;
import org.epragati.ticket.dao.TicketImageDTO;
import org.epragati.ticket.dao.TicketSequnceDAO;
import org.epragati.ticket.dao.TicketsDAO;
import org.epragati.tickets.model.SupportUserCreationVO;
import org.epragati.tickets.model.SupportUserSignUp;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;
import org.epragati.tickets.ticketmapper.ActionEnums;
import org.epragati.tickets.ticketmapper.ModuleEnums;
import org.epragati.tickets.ticketmapper.StatusEnums;
import org.epragati.tickets.ticketmapper.SupportUserMapper;
import org.epragati.tickets.ticketmapper.TicketMapper;
import org.epragati.tickets.ticketmapper.UpdateIndexing;
import org.epragati.util.validators.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Service
public class SaveTicketImp implements SaveTicketIntra {

	@Autowired
	TicketsDAO ticketsDAO;

	@Autowired
	TicketSequnceDAO ticketSequnceDAO;

	@Autowired
	TicketMapper ticketMapper;

	@Autowired
	GridFsOperations operations;

	@Autowired
	UpdateIndexing updateIndexing;

	@Autowired
	OfficeDAO officeDAO;

	@Autowired
	DistrictDAO districtDAO;

	@Autowired
	SupportUserMapper supportUserMapper;

	@Autowired
	SupportUserDAO supportUserDAO;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Value("${commonPasswordSta}")
	private String commonPasswordSta;

	@Autowired
	MandalDAO mandalDAO;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public String saveTicket(TicketVO ticketVo, MultipartFile[] files) throws IOException, BadRequestException {
		TicketDTO ticketDTO = ticketMapper.convertVO(ticketVo);
		if (ObjectUtils.isEmpty(ticketDTO)) {
			throw new BadRequestException("problem ocurred at mapping level : " + ticketVo.getModule());
		}
		if (files != null) {
			ticketDTO.setTicketImageDTO(this.fileStrore(files, ticketDTO));
		}
		Optional<OfficeDTO> officeDTO = officeDAO.findByOfficeCode(ticketVo.getOfficeCode());
		ticketDTO.setDistrictId(officeDTO.get().getDistrict());
		Optional<DistrictDTO> districtDTO = districtDAO.findBydistrictId(ticketDTO.getDistrictId());
		ticketDTO.setDistrictName(districtDTO.get().getDistrictName());
		ticketDTO.setOfficeName(officeDTO.get().getOfficeName());
		ticketDTO.setCreatedDate(ticketMapper.zonedDateConversion());
		ticketDTO.setFirstCreatedRole(ticketDTO.getlUpdatedRole());
		ticketDTO.setFirstCreatedUser(ticketDTO.getTicketUser());
		ticketDTO.setIsTicketLocked(Boolean.FALSE);
		synchronized (ticketDTO.getTicketNo()) {
			ticketsDAO.save(ticketDTO);
		}
		return ticketDTO.getTicketNo();
	}

	// fileStoreUpdation
	public List<TicketImageDTO> fileStrore(MultipartFile[] files, TicketDTO ticketDTO) throws IOException {
		List<TicketImageDTO> imgaeDtolist = new ArrayList<TicketImageDTO>();
		for (MultipartFile file : files) {
			TicketImageDTO imgdto = new TicketImageDTO();
			GridFSFile GridFiles = operations.store(file.getInputStream(), file.getOriginalFilename(),
					this.metadataConversion(ticketDTO));
			imgdto.setImageId(GridFiles.getId().toString());
			imgdto.setImagefileName(file.getOriginalFilename());
			imgaeDtolist.add(imgdto);
		}
		return imgaeDtolist;

	}

	// metaDataUpdation
	public DBObject metadataConversion(TicketDTO ticketDTO) {
		DBObject metadata = new BasicDBObject();
		metadata.put("ticketNumber", ticketDTO.getTicketNo());
		return metadata;
	}

	// gettingQueryByImageId
	public Optional<GridFSDBFile> findingFilesInGridFsById(String id) {

		GridFSDBFile file = operations.findOne(new Query(GridFsCriteria.where("_id").is(id)));

		return Optional.ofNullable(file);
	}

	@Override
	public SupportUserSignUp saveSupportUser(SupportUserCreationVO supportUserCreationVO) {
		SupportUserSignUp signUp = new SupportUserSignUp();
		SupportUserCreationDTO supportDTO = supportUserDAO.findByUserIdOrEmpIdOrMobileNumber(
				supportUserCreationVO.getUserId(), supportUserCreationVO.getEmpId(),
				supportUserCreationVO.getMobileNumber());
		if (supportDTO != null && !StringUtils.isEmpty(supportDTO.getEmpId())) {
			throw new BadRequestException("Based on empId or mobileNo or userid data exits");
		}

		SupportUserCreationDTO supdto = supportUserMapper.convertVo(supportUserCreationVO);
		if (ObjectUtils.isEmpty(supdto)) {
			throw new BadRequestException("Mapping converstion ");
		}
		supportUserDAO.save(supdto);
		signUp.setUserId(supdto.getUserid());
		signUp.setPassword(commonPasswordSta);
		return signUp;
	}

	@Override
	public SupportUserSignUp userSignin(String userId, String password) {
		SupportUserSignUp signUp = new SupportUserSignUp();
		SupportUserCreationDTO supdto = supportUserDAO.findByUserId(userId);
		if (ObjectUtils.isEmpty(supdto)) {
			throw new BadRequestException("User Not Found : " + userId);
		}
		if (!passwordEncoder.matches(password, supdto.getPassword())) {
			throw new BadRequestException("password is not matching ");
		}
		signUp.setUserId(supdto.getUserid());
		signUp.setPrimaryRole(supdto.getPrimaryRoleUser().getRole());
		signUp.setAdditonalRole(
				supdto.getAdditionalRoleUser().stream().map(m -> m.getRole()).collect(Collectors.toList()));
		signUp.setStatus(supdto.getStatus());
		signUp.setDistrictId(supdto.getDistrictId());
		signUp.setDistrict(supdto.getDistrict());
		signUp.setEmpId(supdto.getEmpId());
		signUp.setCreatedDate(supdto.getCreatedDate());
		signUp.setFirstName(supdto.getFirstName());
		signUp.setLastName(supdto.getLastName());
		return signUp;
	}

	public void checkUpdationCompleted(String voRole, String dtorole, String userId) {
		int voRoleIndex = updateIndexing.getIndexFromActionEnums(voRole);
		int dtoRoleIndex = updateIndexing.getIndexFromActionEnums(dtorole);
		if (voRoleIndex == dtoRoleIndex) {
			throw new BadRequestException("Ticket Action already Performed By " + dtorole + " With id : " + userId);
		}
	}

	public void checkUpdationRole(String voRole, String dtoRole, String userId) {
		if (voRole == dtoRole) {
			throw new BadRequestException("Ticket Action already Performed By " + dtoRole + " With id : " + userId);
		}
	}

	@Override
	public String updateTicket(UpdateTicketVO updateVO, MultipartFile[] files, String ticketNo) throws IOException {
		TicketDTO updateTicketDTO = ticketsDAO.findByTicketNo(ticketNo);
		if (ObjectUtils.isEmpty(updateTicketDTO)) {
			throw new BadRequestException("No RECORD FOUND");
		}
		if (files != null) {
			updateTicketDTO.setTicketImageDTO(this.fileStrore(files, updateTicketDTO));
		}
		this.checkUpdationRole(updateVO.getRole(), updateTicketDTO.getlUpdatedRole(),
				updateTicketDTO.getlUpdatedUser());

		/*
		 * if (updateVO.getRole().equals(updateTicketDTO.getlUpdatedRole())) { throw new
		 * BadRequestException("Ticket Action Performed By : " +
		 * updateTicketDTO.getlUpdatedRole() + " _ " +
		 * updateTicketDTO.getlUpdatedUser()); }
		 */

		if (!updateVO.getStatus().equals(StatusEnums.REOPEN.getStatus())) {
			TicketDTO updateIndex = updateIndexing.updationOfCurrentIndex(updateVO.getRole(), updateVO.getStatus());
			updateTicketDTO.setCurrentIndex(updateIndex.getCurrentIndex());
			updateTicketDTO.setCurrentRole(updateIndex.getCurrentRole());
		}
		updateTicketDTO.setUpdateActionRoles(ticketMapper.updateActionRoles(updateVO.getRole(),
				updateTicketDTO.getTicketNo(), updateTicketDTO.getModule(), updateVO.getUser(), updateVO.getStatus(),
				updateVO.getComments(), updateTicketDTO.getUpdateActionRoles()));
		if (updateVO.getStatus().equals(StatusEnums.REOPEN.getStatus())) {

			updateTicketDTO.setCurrentIndex(updateTicketDTO.getPreviousClosedIndex());
			updateTicketDTO.setCurrentRole(updateTicketDTO.getPreviousClosedRole());
			updateTicketDTO.setReopenActionRoles(ticketMapper.reopenActionRoles(updateVO.getRole(), updateVO.getUser(),
					updateVO.getComments(), updateTicketDTO.getReopenActionRoles()));
		}
		updateTicketDTO.setStatus(updateVO.getStatus());
		updateTicketDTO.setlUpdatedRole(updateVO.getRole());
		updateTicketDTO.setlUpdatedUser(updateVO.getUser());
		updateTicketDTO.setlUpdate(ticketMapper.zonedDateConversion());
		ticketsDAO.save(updateTicketDTO);

		return updateVO.getStatus();
	}

	@Override
	public String resetPassword(String userId, String password, String newPassword, String confirmPassword) {
		SupportUserCreationDTO sudto = supportUserDAO.findByUserId(userId);

		if (StringUtils.isEmpty(sudto.getUserid())) {
			throw new BadRequestException("No Record Found");
		}
		if (passwordEncoder.matches(newPassword, sudto.getPassword())) {
			throw new BadRequestException("new password should not be same as  old password ");

		}
		PasswordValidator pwdvali = new PasswordValidator();
		if (!pwdvali.validate(newPassword)) {
			throw new BadRequestException(
					"password must be contain one small letter,one capital letter,one special character,and length should be 6 to 20");
		}
		sudto.setPassword(passwordEncoder.encode(newPassword));
		sudto.setUpdatePassword(false);
		supportUserDAO.save(sudto);
		return "updateSucessfully";
	}

	@Override
	public String cancelTicket(UpdateTicketVO ticketVO) throws IOException {
		TicketDTO newTicketDTO = ticketsDAO.findByTicketNo(ticketVO.getTicketNo());
		if (ObjectUtils.isEmpty(newTicketDTO)) {
			throw new BadRequestException("No Record Found");
		}
		//this.checkUpdationRole(ticketVO.getRole(), newTicketDTO.getlUpdatedRole(), newTicketDTO.getlUpdatedUser());
		if (ticketVO.getStatus().equals(StatusEnums.CLOSED.getStatus())) {
			newTicketDTO.setLockedDetails(null);
			newTicketDTO.setIsTicketLocked(Boolean.FALSE);
			newTicketDTO.setPreviousClosedIndex(updateIndexing.getIndexFromActionEnums(ticketVO.getRole()));
			newTicketDTO.setPreviousClosedRole(ticketVO.getRole());
			newTicketDTO.setCurrentIndex(0);
			newTicketDTO.setCurrentRole(null);
		}
		if (ticketVO.getRole().equals(ActionEnums.SUPPORT.getActionRole())) {
			newTicketDTO.setSuggestionActionRoles(ticketMapper.suggestionActionRoles(ticketVO.getRole(),
					ticketVO.getUser(), ticketVO.getSuggestion(), newTicketDTO.getSuggestionActionRoles()));
		}
		newTicketDTO.setUpdateActionRoles(ticketMapper.updateActionRoles(ticketVO.getRole(), newTicketDTO.getTicketNo(),
				newTicketDTO.getModule(), ticketVO.getUser(), ticketVO.getStatus(), ticketVO.getComments(),
				newTicketDTO.getUpdateActionRoles()));
		newTicketDTO.setClosedBy(ticketVO.getUser());
		newTicketDTO.setClosedLevel(ticketVO.getRole());
		newTicketDTO.setClosedDate(ticketMapper.zonedDateConversion());
		newTicketDTO.setStatus(ticketVO.getStatus());
		newTicketDTO.setlUpdatedRole(ticketVO.getRole());
		newTicketDTO.setlUpdatedUser(ticketVO.getUser());
		newTicketDTO.setlUpdate(ticketMapper.zonedDateConversion());
		if (ticketVO.getStatus().equals(StatusEnums.CANCELLED.getStatus())) {
			newTicketDTO.setCurrentIndex(-1);
			newTicketDTO.setCurrentRole(null);
		}
		ticketsDAO.save(newTicketDTO);
		return ticketVO.getStatus();
	}

	@Override
	public String saveCitizenTicket(TicketVO ticketVO, MultipartFile[] files) throws IOException {
		TicketDTO ticketDTO = ticketMapper.convertVO(ticketVO);
		if (ticketVO.getModule().equalsIgnoreCase(ModuleEnums.SPECIALNUMBER.getModules())) {
			ticketDTO.setStatus(StatusEnums.RTOAPPROVED.getStatus());
			ticketDTO.setCurrentIndex(4);
			ticketDTO.setCurrentRole(ActionEnums.SUPPORT.getActionRole());
		}

		if (ObjectUtils.isEmpty(ticketDTO)) {
			throw new BadRequestException("problem ocurred at mapping level :" + ticketVO.getModule());
		}
		if (files != null) {
			ticketDTO.setTicketImageDTO(this.fileStrore(files, ticketDTO));
		}

		Optional<MandalDTO> mandalDTO = mandalDAO.findByMandalCode(ticketVO.getMandalCode());
		ticketDTO.setOfficeCode(mandalDTO.get().getHsrpoffice());
		ticketDTO.setDistrictId(ticketVO.getDistrictId());
		ticketDTO.setDistrictName(ticketVO.getDistrictName());
		Optional<OfficeDTO> officeDTO = officeDAO.findByOfficeCode(mandalDTO.get().getHsrpoffice());
		ticketDTO.setOfficeName(officeDTO.get().getOfficeName());
		ticketDTO.setCreatedDate(ticketMapper.zonedDateConversion());
		ticketDTO.setFirstCreatedRole(ticketVO.getTicketUser());
		ticketDTO.setFirstCreatedUser(ticketDTO.getTicketUser());
		ticketDTO.setIsTicketLocked(Boolean.FALSE);
		synchronized (ticketDTO.getTicketNo()) {
			ticketsDAO.save(ticketDTO);
		}
		return ticketDTO.getTicketNo();
	}

	@Override
	public String updateSupportUser(SupportUserCreationVO supportUserCreationVO) {

		SupportUserCreationDTO supportDTO = supportUserDAO.findByUserId(supportUserCreationVO.getUserId());
		if (supportDTO == null) {
			throw new BadRequestException("User doesn't exist ");
		}
		Query query = new Query(Criteria.where("userId").is(supportDTO.getUserId()));

		Update update = new Update();
		update.set("module", supportUserCreationVO.getModule());
		update.set("district", supportUserCreationVO.getDistrict());
		update.set("districtId", supportUserCreationVO.getDistrictId());

		mongoTemplate.updateFirst(query, update, SupportUserCreationDTO.class);

		return "Support_User data Updated SuccessFully";

	}

}
