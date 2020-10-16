package org.epragati.tickets.ticketmapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.epragati.common.mappers.BaseMapper;
import org.epragati.ticket.dao.ApplicationIssueDTO;
import org.epragati.ticket.dao.IssueImageDTO;
import org.epragati.ticket.dao.IssueSequenceDAO;
import org.epragati.ticket.dao.IssueSequenceDTO;
import org.epragati.ticket.dao.IssueSolutionDTO;
import org.epragati.ticket.dao.ReopenIssueDTO;
import org.epragati.tickets.model.ApplicationIssueVO;
import org.epragati.tickets.model.IssueImageVO;
import org.epragati.tickets.model.IssueSolutionVO;
import org.epragati.tickets.model.ReopenIssueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationIssueMapper extends BaseMapper<ApplicationIssueDTO, ApplicationIssueVO> {

	@Autowired
	IssueSequenceDAO issueSequenceDAO;

	@Value("${ticket.imgae.url:}")
	private String imagePreUrl;

	public ApplicationIssueDTO convertVO(ApplicationIssueVO vo) {
		ApplicationIssueDTO dto = new ApplicationIssueDTO();
		funPoint(this.issueNumberGeneration(), dto::setIssueNo);
		funPoint(vo.getTicketNo(), dto::setTicketNo);
		funPoint(vo.getRaisedBy(), dto::setRaisedBy);
		funPoint(vo.getIssue(), dto::setIssue);
		funPoint(vo.getStatus(), dto::setStatus);
		funPoint(vo.getProbLevel(), dto::setProbLevel);
		funPoint(vo.getModule(), dto::setModule);
		funPoint(this.dateConversion(), dto::setCreatedDate);
		return dto;
	}

	@Override
	public ApplicationIssueVO convertEntity(ApplicationIssueDTO dto) {
		ApplicationIssueVO vo = new ApplicationIssueVO();
		funPoint(dto.getIssueNo(), vo::setIssueNo);
		funPoint(dto.getTicketNo(), vo::setTicketNo);
		funPoint(dto.getRaisedBy(), vo::setRaisedBy);
		funPoint(dto.getStatus(), vo::setStatus);
		funPoint(dto.getProbLevel(), vo::setProbLevel);
		funPoint(dto.getIssue(), vo::setIssue);
		funPoint(dto.getModule(), vo::setModule);
		funPoint(dto.getCreatedDate(), vo::setCreatedDate);
		if (dto.getIssueImageDTO() != null) {
			List<IssueImageDTO> imgDtoList = dto.getIssueImageDTO();
			List<IssueImageVO> imgVoList = new ArrayList<>();
			for (IssueImageDTO imgdto : imgDtoList) {
				IssueImageVO imgVO = new IssueImageVO();
				imgVO.setImageId(imgdto.getImageId());
				imgVO.setImgaeType(imgdto.getImgaeType());
				imgVO.setImagefileName(imgdto.getImagefileName());
				imgVO.setImageurl(imagePreUrl + "?appImageDocId=" + imgdto.getImageId());
				imgVoList.add(imgVO);
			}
			vo.setIssueImageVO(imgVoList);
		}
		if (ObjectUtils.isNotEmpty(dto.getIssueSolutionDTO())) {
			vo.setIssueSolutionVO(this.gettingIssueSolution(dto.getIssueSolutionDTO()));
		}
		return vo;
	}

	// zonedDateConversion
	public LocalDateTime dateConversion() {
		ZoneId zoneId = ZoneId.of("Asia/Kolkata");
		return LocalDateTime.now(zoneId);
	}

	public String issueNumberGeneration() {
		String issue = "ISSUE";
		String module = "DIS";
		IssueSequenceDTO issueSequence = issueSequenceDAO.findById(issue);
		Long no = issueSequence.getIssueId();
		Long issueSeq = ++no;
		String issueNo = module + issueSeq;
		issueSequence.setIssueId(issueSeq);
		issueSequenceDAO.save(issueSequence);
		return issueNo.toString();
	}

	public List<IssueSolutionDTO> updateIssueSolution(String user, String comment, String status, String ticketNo,
			List<IssueSolutionDTO> oldDetails) {
		List<IssueSolutionDTO> updateList = new ArrayList<IssueSolutionDTO>();
		IssueSolutionDTO issueSolutionDTO = new IssueSolutionDTO();
		issueSolutionDTO.setSolvedBy(user);
		issueSolutionDTO.setSolvedComment(comment);
		issueSolutionDTO.setStatus(status);
		issueSolutionDTO.setTicketNo(ticketNo);
		issueSolutionDTO.setSolvedDate(this.dateConversion());
		updateList.add(issueSolutionDTO);
		if (oldDetails != null) {
			updateList.addAll(oldDetails);
		}
		return updateList;
	}

	public List<IssueSolutionVO> gettingIssueSolution(List<IssueSolutionDTO> issueSolutionDTOList) {
		List<IssueSolutionVO> voList = new ArrayList<IssueSolutionVO>();
		issueSolutionDTOList.forEach(a -> {
			IssueSolutionVO vo = new IssueSolutionVO();
			funPoint(a.getSolvedBy(), vo::setSolvedBy);
			funPoint(a.getSolvedComment(), vo::setSolvedComment);
			funPoint(a.getStatus(), vo::setStatus);
			funPoint(a.getTicketNo(), vo::setTicketNo);
			funPoint(a.getSolvedDate(), vo::setSolvedDate);
			voList.add(vo);
		});

		return voList;
	}

	public List<ReopenIssueDTO> saveReopenDetails(String user,String comments,String ticketNo, List<ReopenIssueDTO> oldDetails) {
		List<ReopenIssueDTO> reOpenList = new ArrayList<ReopenIssueDTO>();
		ReopenIssueDTO dto = new ReopenIssueDTO();
		dto.setReOpenBy(user);
		dto.setReOpenComment(comments);
		dto.setReOpenDate(this.dateConversion());
		dto.setStatus("REOPEN");
		dto.setTicketNo(ticketNo);
		reOpenList.add(dto);
		if(oldDetails!=null) {
			reOpenList.addAll(reOpenList);
		}
		return reOpenList;
	}
	public List<ReopenIssueVO> gettingReopenIssueDetails(List<ReopenIssueDTO> reopenIssueDTO){
		List<ReopenIssueVO> voList= new ArrayList<ReopenIssueVO>();
		reopenIssueDTO.forEach(a->{
			ReopenIssueVO vo = new ReopenIssueVO();
			funPoint(a.getReOpenBy(), vo::setReOpenBy);
			funPoint(a.getReOpenComment(), vo::setReOpenComment);
			funPoint(a.getReOpenDate(),vo::setReOpenDate);
			funPoint(a.getStatus(), vo::setStatus);
			funPoint(a.getTicketNo(), vo::setTicketNo);
			voList.add(vo);
			
		});
		
		return voList;
	}
}
