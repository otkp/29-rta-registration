package org.epragati.tickets.ticketmapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.epragati.common.mappers.BaseMapper;
import org.epragati.ticket.dao.PrimaryRoleUser;
import org.epragati.ticket.dao.SupportUserCreationDTO;
import org.epragati.tickets.model.SupportUserCreationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SupportUserMapper extends BaseMapper<SupportUserCreationDTO, SupportUserCreationVO> {

	@Value("${commonPassword}")
	private String commonPassword;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UpdateIndexing updateIndexing;

	@Override
	public SupportUserCreationVO convertEntity(SupportUserCreationDTO dto) {

		return null;
	}

	public SupportUserCreationDTO convertVo(SupportUserCreationVO vo) {

		SupportUserCreationDTO supportUserCreationDTO = new SupportUserCreationDTO();
		funPoint(vo.getFirstName(),supportUserCreationDTO::setFirstName);
		funPoint(vo.getLastName(),supportUserCreationDTO::setLastName);
		funPoint(vo.getEmpId(),supportUserCreationDTO::setEmpId);
		funPoint(vo.getEmpDesgn(),supportUserCreationDTO::setEmpDesgn);
		funPoint(vo.getUserId(),supportUserCreationDTO::setUserid);
		funPoint(vo.getDistrictId(), supportUserCreationDTO::setDistrictId);		
		funPoint(vo.getDistrict(), supportUserCreationDTO::setDistrict);
		funPoint(vo.getModule(), supportUserCreationDTO::setModule);
		funPoint(vo.getRole(),supportUserCreationDTO::setRole);
		funPoint(vo.getAadharNumber(),supportUserCreationDTO::setAadharNumber);
		funPoint(vo.getMobileNumber(),supportUserCreationDTO::setMobileNumber);
		funPoint(vo.getEmpEmail(),supportUserCreationDTO::setEmpEmail);
		funPoint(vo.getStatus(),supportUserCreationDTO::setStatus);
		funPoint(LocalDateTime.now(),supportUserCreationDTO::setCreatedDate);
		funPoint(commonPassword,supportUserCreationDTO::setPassword);
		funPoint(this.getUpdatePrimaryRole(ActionEnums.SUPPORT.getActionRole()),supportUserCreationDTO::setPrimaryRoleUser);
		funPoint((this.getAdditionalRolesUpdate(vo.getLevel())),supportUserCreationDTO::setAdditionalRoleUser);
		return supportUserCreationDTO;
	}

	public PrimaryRoleUser getUpdatePrimaryRole(String role) {
		PrimaryRoleUser pu = new PrimaryRoleUser();
		pu.setRoleId(4);
		pu.setRole(role);
		return pu;
	}

	public List<PrimaryRoleUser> getAdditionalRolesUpdate(List<String> level) {
		List<PrimaryRoleUser> puList = new ArrayList<PrimaryRoleUser>();
		for (String role : level) {	
			PrimaryRoleUser pu = new PrimaryRoleUser();
			    if(role.equals("L1")) {
				pu.setRole(role);	
				pu.setRoleId(updateIndexing.getIndexFromActionEnums("SUPPORT"));
			    }
			    if(role.equals("L2")) {
			    	pu.setRole(role);	
					pu.setRoleId(updateIndexing.getIndexFromActionEnums(role));
			    }
			    if(role.equals("L3")) {
			    	pu.setRole(role);	
					pu.setRoleId(updateIndexing.getIndexFromActionEnums(role));
			    }
				puList.add(pu);
		}	
		return puList;
	}

}
