package org.epragati.tickets.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SuggestionActionRoles {

	private String role;
	private String user;
	private String suggestion;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private LocalDateTime suggestedDate;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public LocalDateTime getSuggestedDate() {
		return suggestedDate;
	}

	public void setSuggestedDate(LocalDateTime suggestedDate) {
		this.suggestedDate = suggestedDate;
	}

}
