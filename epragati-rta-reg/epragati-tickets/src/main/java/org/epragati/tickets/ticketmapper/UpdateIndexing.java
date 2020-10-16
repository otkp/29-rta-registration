package org.epragati.tickets.ticketmapper;

import org.epragati.ticket.dao.TicketDTO;
import org.springframework.stereotype.Component;

@Component
public class UpdateIndexing {

	public TicketDTO updationOfCurrentIndex(String role, String getstatusEnum) {
		TicketDTO ticketDTO = new TicketDTO();
		StatusEnums statusenum = StatusEnums.valueOf(getstatusEnum);
		switch (statusenum) {
		case NEW:
			ticketDTO.setCurrentIndex(1);
			ticketDTO.setCurrentRole(ActionEnums.CCO.getActionRole());
			return ticketDTO;
		case CCOAPPROVED:
			ticketDTO.setCurrentIndex(2);
			ticketDTO.setCurrentRole(ActionEnums.AO.getActionRole());
			return ticketDTO;
		case AOAPPROVED:
			ticketDTO.setCurrentIndex(3);
			ticketDTO.setCurrentRole(ActionEnums.RTO.getActionRole());
			return ticketDTO;
		case RTOAPPROVED:
			ticketDTO.setCurrentIndex(4);
			ticketDTO.setCurrentRole(ActionEnums.SUPPORT.getActionRole());
			return ticketDTO;
		case MVIAPPROVED:
			ticketDTO.setCurrentIndex(4);
			ticketDTO.setCurrentRole(ActionEnums.SUPPORT.getActionRole());
			return ticketDTO;
		case DTCAPPROVED:
			ticketDTO.setCurrentIndex(4);
			ticketDTO.setCurrentRole(ActionEnums.SUPPORT.getActionRole());
			return ticketDTO;
		case CLOSED:
			ticketDTO.setCurrentIndex(0);
			ticketDTO.setCurrentRole(null);
			return ticketDTO;
		case CANCELLED:
			ticketDTO.setCurrentIndex(-1);
			ticketDTO.setCurrentRole(null);
			return ticketDTO;
		default:
			break;
		}
		return null;

	}

	public int getIndexFromActionEnums(String role) {
		ActionEnums actionenum = ActionEnums.valueOf(role);
		switch (actionenum) {
		case CCO:
			return 1;
		case AO:
			return 2;
		case RTO:
			return 3;
		case DEALER:
			return 1;
		case FINANCIER:
			return 1;
		case SUPPORT:
			return 4;
		case L2:
			return 5;
		case L3:
			return 6;
		default:
			break;
		}
		return -1;

	}

}