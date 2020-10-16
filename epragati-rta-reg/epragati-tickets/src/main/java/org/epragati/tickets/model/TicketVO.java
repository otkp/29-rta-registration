package org.epragati.tickets.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketVO {
	private String module;
	private String subModule;
	private String issueType;
	private String problemLevel;
	private String problemOcurredAt;
	private String ticketNo;
	private String ticketUser;
	private String mobileNo;
	private Integer districtId;
	private String districtName;
	private String officeCode;
	private String officeName;
	private String probDesc;
	private String status;
	private String trNo;
	private String prNo;
	private String regapplicationNo;
	private String chassisNumber;
	private String engineNumber;
	private String dlNo;
	private String llrNo;
	private String dlApplicationNo;
	private String dlAadharNo;
	//private List<Map<String, String>> problemDesc;
	private String closedBy;
	private String closedLevel;
	private String createdRole;
	private String currentRole;
	private String firstCreatedRole;
	private String firstCreatedUser;
	private String request;
	private String searchBy;
	private String approvedUserId;
	private String approvedRole;
	private String lUpdatedUser;
	private String lUpdatedRole;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime lUpdate;
	private Boolean isFromDept;
	private List<UpdateActionRolesVO> upDateActionRoles;
	private List<ReopenActionDetailsVO> reopenActionDetailsVO;
	private List<SuggestionActionRoles> suggestionActionRoles;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime closedDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime createdDate;
	private List<TicketImageVO> ticketImageVO;
	private Integer mandalCode;

	// generategetterAndsetters
	public String getApprovedUserId() {
		return approvedUserId;
	}

	public void setApprovedUserId(String approvedUserId) {
		this.approvedUserId = approvedUserId;
	}

	public String getApprovedRole() {
		return approvedRole;
	}

	public void setApprovedRole(String approvedRole) {
		this.approvedRole = approvedRole;
	}

	public List<UpdateActionRolesVO> getUpDateActionRoles() {
		return upDateActionRoles;
	}

	public void setUpDateActionRoles(List<UpdateActionRolesVO> upDateActionRoles) {
		this.upDateActionRoles = upDateActionRoles;
	}

	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public String getFirstCreatedRole() {
		return firstCreatedRole;
	}

	public void setFirstCreatedRole(String firstCreatedRole) {
		this.firstCreatedRole = firstCreatedRole;
	}

	public String getFirstCreatedUser() {
		return firstCreatedUser;
	}

	public void setFirstCreatedUser(String firstCreatedUser) {
		this.firstCreatedUser = firstCreatedUser;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public List<TicketImageVO> getTicketImageVO() {
		return ticketImageVO;
	}

	public void setTicketImageVO(List<TicketImageVO> ticketImageVO) {
		this.ticketImageVO = ticketImageVO;
	}

	public String getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}

	public String getClosedLevel() {
		return closedLevel;
	}

	public void setClosedLevel(String closedLevel) {
		this.closedLevel = closedLevel;
	}

	public LocalDateTime getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(LocalDateTime closedDate) {
		this.closedDate = closedDate;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSubModule() {
		return subModule;
	}

	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getProblemLevel() {
		return problemLevel;
	}

	public void setProblemLevel(String problemLevel) {
		this.problemLevel = problemLevel;
	}

	public String getProblemOcurredAt() {
		return problemOcurredAt;
	}

	public void setProblemOcurredAt(String problemOcurredAt) {
		this.problemOcurredAt = problemOcurredAt;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getTicketUser() {
		return ticketUser;
	}

	public void setTicketUser(String ticketUser) {
		this.ticketUser = ticketUser;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getProbDesc() {
		return probDesc;
	}

	public void setProbDesc(String probDesc) {
		this.probDesc = probDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTrNo() {
		return trNo;
	}

	public void setTrNo(String trNo) {
		this.trNo = trNo;
	}

	public String getPrNo() {
		return prNo;
	}

	public void setPrNo(String prNo) {
		this.prNo = prNo;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getDlNo() {
		return dlNo;
	}

	public void setDlNo(String dlNo) {
		this.dlNo = dlNo;
	}

	public String getLlrNo() {
		return llrNo;
	}

	public void setLlrNo(String llrNo) {
		this.llrNo = llrNo;
	}

	public String getRegapplicationNo() {
		return regapplicationNo;
	}

	public void setRegapplicationNo(String regapplicationNo) {
		this.regapplicationNo = regapplicationNo;
	}

	public String getDlApplicationNo() {
		return dlApplicationNo;
	}

	public void setDlApplicationNo(String dlApplicationNo) {
		this.dlApplicationNo = dlApplicationNo;
	}

	public String getDlAadharNo() {
		return dlAadharNo;
	}

	public void setDlAadharNo(String dlAadharNo) {
		this.dlAadharNo = dlAadharNo;
	}

	public String getlUpdatedUser() {
		return lUpdatedUser;
	}

	public void setlUpdatedUser(String lUpdatedUser) {
		this.lUpdatedUser = lUpdatedUser;
	}

	public String getlUpdatedRole() {
		return lUpdatedRole;
	}

	public void setlUpdatedRole(String lUpdatedRole) {
		this.lUpdatedRole = lUpdatedRole;
	}

	public LocalDateTime getlUpdate() {
		return lUpdate;
	}

	public void setlUpdate(LocalDateTime lUpdate) {
		this.lUpdate = lUpdate;
	}

	public List<ReopenActionDetailsVO> getReopenActionDetailsVO() {
		return reopenActionDetailsVO;
	}

	public void setReopenActionDetailsVO(List<ReopenActionDetailsVO> reopenActionDetailsVO) {
		this.reopenActionDetailsVO = reopenActionDetailsVO;
	}

	public String getCreatedRole() {
		return createdRole;
	}

	public void setCreatedRole(String createdRole) {
		this.createdRole = createdRole;
	}

	public Boolean getIsFromDept() {
		return isFromDept;
	}

	public void setIsFromDept(Boolean isFromDept) {
		this.isFromDept = isFromDept;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<SuggestionActionRoles> getSuggestionActionRoles() {
		return suggestionActionRoles;
	}

	public void setSuggestionActionRoles(List<SuggestionActionRoles> suggestionActionRoles) {
		this.suggestionActionRoles = suggestionActionRoles;
	}

	public Integer getMandalCode() {
		return mandalCode;
	}

	public void setMandalCode(Integer mandalCode) {
		this.mandalCode = mandalCode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}

}
