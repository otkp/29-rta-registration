package org.epragati.tickets.service;

import java.util.List;

import org.epragati.tickets.model.LockedTicketsVO;
import org.epragati.tickets.model.SupportUserLimited;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;

public interface SupportReportsIntra {

	List<TicketVO> SupportTicketsView(String user, String role);

	Long SupportTicketsPendingCount(String user, String role);

	List<LockedTicketsVO> lockedTicketDetails(int districtId);
	
	List<TicketVO> SupportAssignedTicketsView(String user, String role);
	
	List<SupportUserLimited> getAllUsers();

	List<TicketVO> getSpecialNumberTicketData(String user, String role);

	Long specialTicketsPendingCount(String user, String role);
	
	String assignedTicket(UpdateTicketVO assignedTicketVo);
	
	int assignedTicketsCount(String user, String role);
}
