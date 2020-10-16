package org.epragati.tickets.service;

import java.io.IOException;
import java.util.Optional;

import org.epragati.tickets.model.SupportUserCreationVO;
import org.epragati.tickets.model.SupportUserSignUp;
import org.epragati.tickets.model.TicketVO;
import org.epragati.tickets.model.UpdateTicketVO;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;

public interface SaveTicketIntra {

	String saveTicket(TicketVO ticketVo,MultipartFile[] files) throws IOException;
	
	String updateTicket(UpdateTicketVO ticketVO, MultipartFile[] files, String ticketNo)throws IOException;
	
	String cancelTicket(UpdateTicketVO ticketVO) throws IOException;
    
	SupportUserSignUp saveSupportUser(SupportUserCreationVO supportUserCreationVO);
	
	SupportUserSignUp userSignin(String userId,String password);
	
	public String resetPassword(String userId, String password, String newPassword, String confirmPassword);
	
	Optional<GridFSDBFile> findingFilesInGridFsById(String id);

	String saveCitizenTicket(TicketVO ticketVO,MultipartFile[] files) throws IOException;
	
	String updateSupportUser(SupportUserCreationVO supportUserCreationVO);

}
