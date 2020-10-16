package org.epragati.tickets.model;

public class RaisedCount {
	
	private String firstCreatedRole;
	private Long count;
	private Long currentIndex;
	private String approvedLevels;
	
	public String getApprovedLevels() {
		return approvedLevels;
	}
	public void setApprovedLevels(String approvedLevels) {
		this.approvedLevels = approvedLevels;
	}
	public String getFirstCreatedRole() {
		return firstCreatedRole;
	}
	public void setFirstCreatedRole(String firstCreatedRole) {
		this.firstCreatedRole = firstCreatedRole;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Long getCurrentIndex() {
		return currentIndex;
	}
	public void setCurrentIndex(Long currentIndex) {
		this.currentIndex = currentIndex;
	}
	

}
