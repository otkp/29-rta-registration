package org.epragati.tickets.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReopenIssueVO {

	private String reOpenBy;
	private String reOpenComment;
	private String status;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime reOpenDate;
	private String ticketNo;

	// Getters & Setters
	public String getReOpenBy() {
		return reOpenBy;
	}

	public void setReOpenBy(String reOpenBy) {
		this.reOpenBy = reOpenBy;
	}

	public String getReOpenComment() {
		return reOpenComment;
	}

	public void setReOpenComment(String reOpenComment) {
		this.reOpenComment = reOpenComment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getReOpenDate() {
		return reOpenDate;
	}

	public void setReOpenDate(LocalDateTime reOpenDate) {
		this.reOpenDate = reOpenDate;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

}
