package org.epragati.tickets.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApplicationIssueVO {

	private String issueNo;
	private String issue;
	private String ticketNo;
	private String raisedBy;
	private String status;
	private String probLevel;
	private String module;
	private List<IssueImageVO> issueImageVO;
	private List<IssueSolutionVO> issueSolutionVO;
	private List<ReopenIssueVO> reopenIssueVO;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime createdDate;

	// Getters & Setters
	public String getIssueNo() {
		return issueNo;
	}

	public void setIssueNo(String issueNo) {
		this.issueNo = issueNo;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getRaisedBy() {
		return raisedBy;
	}

	public void setRaisedBy(String raisedBy) {
		this.raisedBy = raisedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProbLevel() {
		return probLevel;
	}

	public void setProbLevel(String probLevel) {
		this.probLevel = probLevel;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public List<IssueImageVO> getIssueImageVO() {
		return issueImageVO;
	}

	public void setIssueImageVO(List<IssueImageVO> issueImageVO) {
		this.issueImageVO = issueImageVO;
	}

	public List<IssueSolutionVO> getIssueSolutionVO() {
		return issueSolutionVO;
	}

	public void setIssueSolutionVO(List<IssueSolutionVO> issueSolutionVO) {
		this.issueSolutionVO = issueSolutionVO;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public List<ReopenIssueVO> getReopenIssueVO() {
		return reopenIssueVO;
	}

	public void setReopenIssueVO(List<ReopenIssueVO> reopenIssueVO) {
		this.reopenIssueVO = reopenIssueVO;
	}

}
