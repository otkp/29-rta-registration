package org.epragati.tickets.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReopenActionDetailsVO {

	private String reOpenBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime reOpenDate;
	private String reOpenRole;
	private String reOpenComments;

	public String getReOpenBy() {
		return reOpenBy;
	}

	public void setReOpenBy(String reOpenBy) {
		this.reOpenBy = reOpenBy;
	}

	public LocalDateTime getReOpenDate() {
		return reOpenDate;
	}

	public void setReOpenDate(LocalDateTime reOpenDate) {
		this.reOpenDate = reOpenDate;
	}

	public String getReOpenRole() {
		return reOpenRole;
	}

	public void setReOpenRole(String reOpenRole) {
		this.reOpenRole = reOpenRole;
	}

	public String getReOpenComments() {
		return reOpenComments;
	}

	public void setReOpenComments(String reOpenComments) {
		this.reOpenComments = reOpenComments;
	}

}
