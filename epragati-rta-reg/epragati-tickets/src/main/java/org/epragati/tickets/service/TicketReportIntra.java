package org.epragati.tickets.service;

import java.util.List;

import org.epragati.tickets.model.ClosedView;
import org.epragati.tickets.model.DistrictLevelCount;
import org.epragati.tickets.model.ModuleBaseCount;
import org.epragati.tickets.model.TicketCounts;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;

public interface TicketReportIntra {

	TicketVO getTicketData(String ticketNo);

	List<TicketVO> getFirstTicketByOfficeCodeandRole(String officeCode, String currentRole);

	List<TicketVO> getFirstTicketByOfficeCode(String officeCode, List<Integer> index);

	TicketCounts getAllLevelReports(String officeCode);
	
	TicketCounts DealerAndFincierTicketCounts(String officeCode,String user,String role);
	
	List<DistrictLevelCount> getPendingCountInDistrict(Integer index);
	
	List<ClosedView> getClosedTicketsVO(String officeCode, String status,String userId);
	
	public List<TicketVO> getTicketDataList(String ticketNo);
	
	List<ModuleBaseCount> getSupportClosedTicketBasedOnModule(String status,String user);
	
	List<TicketVO> getCitizenTicketData(UpdateTicketVO updateTicketVO);
}
