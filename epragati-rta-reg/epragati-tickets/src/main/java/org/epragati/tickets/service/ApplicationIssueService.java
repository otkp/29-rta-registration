package org.epragati.tickets.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.epragati.exception.BadRequestException;
import org.epragati.ticket.dao.ApplicationIssueDAO;
import org.epragati.ticket.dao.ApplicationIssueDTO;
import org.epragati.ticket.dao.IssueImageDTO;
import org.epragati.tickets.model.ApplicationIssueVO;
import org.epragati.tickets.model.UpdateIssueVO;
import org.epragati.tickets.ticketmapper.ApplicationIssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSFile;

@Service
public class ApplicationIssueService implements ApplicationIssueServiceIntra {

	@Autowired
	ApplicationIssueDAO applicationIssueDAO;

	@Autowired
	ApplicationIssueMapper applicationIssueMapper;

	@Autowired
	GridFsOperations operations;

	@Override
	public String saveIssueDetails(ApplicationIssueVO applicationIssueVO, MultipartFile[] files)
			throws IOException, BadRequestException {
		ApplicationIssueDTO dto = applicationIssueMapper.convertVO(applicationIssueVO);
		if (ObjectUtils.isEmpty(files)) {
			dto.setIssueImageDTO(this.issueFileStrore(files, dto));
		}
		synchronized (dto.getTicketNo()) {
			applicationIssueDAO.save(dto);
		}
		return "Issue No : " + dto.getIssueNo() + " is Raised Successfully";
	}

	// fileStoreUpdation
	public List<IssueImageDTO> issueFileStrore(MultipartFile[] files, ApplicationIssueDTO dto) throws IOException {
		List<IssueImageDTO> imgaeDtolist = new ArrayList<IssueImageDTO>();
		for (MultipartFile file : files) {
			IssueImageDTO imgdto = new IssueImageDTO();
			GridFSFile GridFiles = operations.store(file.getInputStream(), file.getOriginalFilename(), "ImageSaved");
			imgdto.setImageId(GridFiles.getId().toString());
			imgdto.setImagefileName(file.getOriginalFilename());
			imgaeDtolist.add(imgdto);
		}
		return imgaeDtolist;

	}

	@Override
	public String updateIssueDetails(UpdateIssueVO updateIssueVO) {
		ApplicationIssueDTO updateDTO = applicationIssueDAO.findByIssueNo(updateIssueVO.getIssueNo());
		if (ObjectUtils.isEmpty(updateDTO)) {
			throw new BadRequestException("No Records Found With Issue No : " + updateIssueVO.getIssueNo());
		}if(updateIssueVO.getStatus().equalsIgnoreCase("newIssue")) {
			updateDTO.setReopenIssueDTO(applicationIssueMapper.saveReopenDetails(updateIssueVO.getUserId(),
					updateIssueVO.getComments(), updateIssueVO.getTicketNo(), updateDTO.getReopenIssueDTO()));
		}else {
		updateDTO.setIssueSolutionDTO(applicationIssueMapper.updateIssueSolution(updateIssueVO.getUserId(), updateIssueVO.getComments(),
				updateIssueVO.getStatus(), updateIssueVO.getTicketNo(), updateDTO.getIssueSolutionDTO()));
		}
		updateDTO.setlUpdatedBy(updateIssueVO.getUserId());
		updateDTO.setlUpdatedDate(applicationIssueMapper.dateConversion());
		updateDTO.setStatus(updateIssueVO.getStatus());
		applicationIssueDAO.save(updateDTO);
		return "Success";
	}

	@Override
	public List<ApplicationIssueVO> getListOfIssuesData(String status) {
		String solved = "issueSolved";
		if (status.equalsIgnoreCase(solved)) {
			List<ApplicationIssueDTO> dtoList = applicationIssueDAO.findByStatusOrderByLUpdatedDateDesc(status);
			if (ObjectUtils.isEmpty(dtoList)) {
				throw new BadRequestException("No Records Found");
			}
			List<ApplicationIssueVO> voList = applicationIssueMapper.convertEntity(dtoList);
			return voList;
		} else {
			List<ApplicationIssueDTO> dtoList = applicationIssueDAO.findByStatus(status);
			if (ObjectUtils.isEmpty(dtoList)) {
				throw new BadRequestException("No Records Found");
			}
			List<ApplicationIssueVO> voList = applicationIssueMapper.convertEntity(dtoList);
			return voList;
		}
	}

	@Override
	public List<ApplicationIssueVO> getSingleIssueData(String issueNo) {
		List<ApplicationIssueVO> voList = new ArrayList<ApplicationIssueVO>();
		ApplicationIssueDTO dto = applicationIssueDAO.findByIssueNo(issueNo);
		if (StringUtils.isEmpty(dto)) {
			throw new BadRequestException("No Records Found with Issue No : " + issueNo);
		}
		ApplicationIssueVO vo = applicationIssueMapper.convertEntity(dto);
		voList.add(vo);
		return voList;
	}

	/*
	 * @Override public String reopenIssue(UpdateIssueVO updateIssueVO) {
	 * ApplicationIssueDTO updateDTO =
	 * applicationIssueDAO.findByIssueNo(updateIssueVO.getIssueNo()); if
	 * (ObjectUtils.isEmpty(updateDTO)) { throw new
	 * BadRequestException("No Records Found with IssueNo : " +
	 * updateIssueVO.getIssueNo()); }
	 * updateDTO.setReopenIssueDTO(applicationIssueMapper.saveReopenDetails(
	 * updateIssueVO.getUserId(), updateIssueVO.getComments(),
	 * updateIssueVO.getTicketNo(), updateDTO.getReopenIssueDTO()));
	 * updateDTO.setlUpdatedBy(updateIssueVO.getUserId());
	 * updateDTO.setlUpdatedDate(applicationIssueMapper.dateConversion());
	 * updateDTO.setStatus(updateIssueVO.getStatus());
	 * applicationIssueDAO.save(updateDTO);
	 * 
	 * return "Issue Reopen Successfully"; }
	 */

}
