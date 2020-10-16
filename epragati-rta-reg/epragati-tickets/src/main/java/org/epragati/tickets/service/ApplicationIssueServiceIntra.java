package org.epragati.tickets.service;

import java.io.IOException;
import java.util.List;

import org.epragati.exception.BadRequestException;
import org.epragati.tickets.model.ApplicationIssueVO;
import org.epragati.tickets.model.UpdateIssueVO;
import org.springframework.web.multipart.MultipartFile;

public interface ApplicationIssueServiceIntra {

	String saveIssueDetails(ApplicationIssueVO applicationIssueVO, MultipartFile[] files)
			throws IOException, BadRequestException;
	
	String updateIssueDetails(UpdateIssueVO updateIssueVO);
	
	List<ApplicationIssueVO> getListOfIssuesData(String status);
	
	List<ApplicationIssueVO> getSingleIssueData(String issueNo);
	
//	String reopenIssue(UpdateIssueVO updateIssueVO);

}
