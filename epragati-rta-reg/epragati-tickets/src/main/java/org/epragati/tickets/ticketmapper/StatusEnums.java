package org.epragati.tickets.ticketmapper;

import java.util.Arrays;
import java.util.List;

public enum StatusEnums {

	NEW(1, "NEW"), CCOAPPROVED(2, "CCOAPPROVED"), AOAPPROVED(3, "AOAPPROVED"), RTOAPPROVED(4, "RTOAPPROVED"),
	CANCELLED(5, "CANCELLED"), CLOSED(0, "CLOSED"), STA(6, "STA"),REOPEN(7,"REOPEN"),ASSIGNED(8,"ASSIGNED"),
	MVIAPPROVED(9,"MVIAPPROVED"),DTCAPPROVED(10,"DTCAPPROVED");

	private Integer statusId;
	private String status;

	private StatusEnums(Integer statusId, String status) {
		this.statusId = statusId;
		this.status = status;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	static List<String> getAllStatusdesc() {
		return Arrays.asList(NEW.getStatus(), CCOAPPROVED.getStatus(), AOAPPROVED.getStatus(),

				RTOAPPROVED.getStatus(), CANCELLED.getStatus(), STA.getStatus(), CLOSED.getStatus(),REOPEN.getStatus());
	}
}
