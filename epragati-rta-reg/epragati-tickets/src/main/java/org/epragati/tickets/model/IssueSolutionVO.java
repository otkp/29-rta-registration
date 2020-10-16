package org.epragati.tickets.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class IssueSolutionVO {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime solvedDate;
	private String solvedComment;
	private String solvedBy;
	private String ticketNo;
	private String status;

	public LocalDateTime getSolvedDate() {
		return solvedDate;
	}

	public void setSolvedDate(LocalDateTime solvedDate) {
		this.solvedDate = solvedDate;
	}

	public String getSolvedComment() {
		return solvedComment;
	}

	public void setSolvedComment(String solvedComment) {
		this.solvedComment = solvedComment;
	}

	public String getSolvedBy() {
		return solvedBy;
	}

	public void setSolvedBy(String solvedBy) {
		this.solvedBy = solvedBy;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
