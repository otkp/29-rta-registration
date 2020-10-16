package org.epragati.tickets.ticketmapper;

import java.util.Arrays;
import java.util.List;

public  enum ActionEnums {

	CCO(1, "CCO"), AO(2, "AO"), RTO(3, "RTO"), SUPPORT(4, "SUPPORT"), L2(5, "L2"), L3(6, "L3"),
    DEALER(7,"DEALER"),FINANCIER(8,"FINANCIER"),MVI(9,"MVI"),DTC(10,"DTC"),CITIZEN(11,"CITIZEN");
	private Integer actionId;
	private String actionRole;

	private ActionEnums(Integer actionId, String actionRole) {
		this.actionId = actionId;
		this.actionRole = actionRole;
	}

	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	public String getActionRole() {
		return actionRole;
	}

	public void setActionRole(String actionRole) {
		this.actionRole = actionRole;
	}

	public List<String> getAllActionRoles(){
		return Arrays.asList(CCO.getActionRole(),AO.getActionRole(),
				RTO.getActionRole());
	}
  
}
