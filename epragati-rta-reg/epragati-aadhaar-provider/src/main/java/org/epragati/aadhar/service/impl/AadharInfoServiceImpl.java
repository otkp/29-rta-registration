package org.epragati.aadhar.service.impl;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Objects;

import org.epragati.aadhaar.AadharDetailsResponseVO;
import org.epragati.aadhaar.AadharServiceResponseModel;
import org.epragati.aadhar.AadhaarProvider;
import org.epragati.aadhar.DateUtil;
import org.epragati.aadhar.ResponseStatus;
import org.epragati.aadhar.Entity.AadhaarRequestLogEntity;
import org.epragati.aadhar.Entity.AadhaarTransactionDTO;
import org.epragati.aadhar.service.AadhaService;
import org.epragati.aadhar.service.AadharInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation for the AadharInfoService. Class calls aadhar api and get
 * response.
 * 
 * @author naga.pulaparthi
 * 
 *
 */
@Service
public class AadharInfoServiceImpl implements AadharInfoService{

	private final AadhaService aadhaTCSService;
	private static final Logger log = LoggerFactory.getLogger(AadharInfoServiceImpl.class);

//	@Autowired
//	private AadhaarTransactionDAO aadhaarTransactionDAO;

	@Autowired
	public AadharInfoServiceImpl(final AadhaService aadharService) {
		this.aadhaTCSService = aadharService;
	}

	@Override
	@Transactional
	public AadharDetailsResponseVO AADHAAR_EKYC_FINGER_AUTHENTICATION(URL endpoint, String uid, String tid, String udc, String rdsId, String ip, String srt, String crt, String skey, String pid, String hmac, String ci, String bt, String pincode, String version, String scheme, String department, String service, String dpId, String rdsVer, String consentme, String userName, String deviceName, String attemptType, String dc, String mi, String mc)
			throws RemoteException, FileNotFoundException, UnsupportedEncodingException, org.hibernate.service.spi.ServiceException, javax.xml.rpc.ServiceException
	{
		if ((Objects.isNull(endpoint)) || (Objects.isNull(uid)) || (Objects.isNull(tid)) || (Objects.isNull(udc)) || (Objects.isNull(rdsId)) || 
				(Objects.isNull(ip)) || (Objects.isNull(srt)) || (Objects.isNull(crt)) || (Objects.isNull(skey)) || 
				(Objects.isNull(pid)) || (Objects.isNull(hmac)) || (Objects.isNull(ci)) || (Objects.isNull(bt)) || 
				(Objects.isNull(pincode)) || (Objects.isNull(version)) || (Objects.isNull(scheme)) || 
				(Objects.isNull(department)) || (Objects.isNull(service)) || (Objects.isNull(dpId)) || 
				(Objects.isNull(rdsVer)) || (Objects.isNull(consentme)) || (Objects.isNull(attemptType)) || 
				(Objects.isNull(dc)) || (Objects.isNull(mi)) || (Objects.isNull(mc))) {
			throw new IllegalArgumentException(" uid,  tid,  udc,  rdsId,  ip,  srt,  crt,  skey,  pid,hmac,  ci,  bt,  pincode,  version,  scheme,  department, service,  dpId, attemptType, rdsVer and consentme can't be null");
		}
		String requestTime = DateUtil.getDate("MM/dd/yyyy hh:mm:ss.SSS a", "IST", new Date());
		AadhaarRequestLogEntity aadhaarRequestLogEntity = new AadhaarRequestLogEntity();
		aadhaarRequestLogEntity.setReqEndPointUrl(endpoint.toString());
		aadhaarRequestLogEntity.setReqUid(uid);
		aadhaarRequestLogEntity.setReqTid(tid);
		aadhaarRequestLogEntity.setReqProvider(AadhaarProvider.APTONLINE.getLabel());
		aadhaarRequestLogEntity.setReqAgencyCode(department);
		aadhaarRequestLogEntity.setReqAgencyName(scheme);
		aadhaarRequestLogEntity.setReqDeviceCode(udc);
		aadhaarRequestLogEntity.setReqDeviceName(deviceName);
		aadhaarRequestLogEntity.setReqTime(requestTime);
		aadhaarRequestLogEntity.setCreatedBy(userName);
		aadhaarRequestLogEntity.setCreatedOn(DateUtil.toCurrentUTCTimeStamp());
		aadhaarRequestLogEntity.setReqServerDateTime(srt);
		aadhaarRequestLogEntity.setReqClientDateTime(crt);
		aadhaarRequestLogEntity.setReqRdsVer(rdsVer);
		aadhaarRequestLogEntity.setReqConsentMe(consentme);
		aadhaarRequestLogEntity.setReqService(service);
		aadhaarRequestLogEntity.setReqAttemptType(attemptType);
		aadhaarRequestLogEntity.setReqAuthType(bt);
		aadhaarRequestLogEntity.setReqRdsId(rdsId);
		aadhaarRequestLogEntity.setReqDpId(dpId);
		aadhaarRequestLogEntity.setReqDc(dc);
		aadhaarRequestLogEntity.setReqMi(mi);
		aadhaarRequestLogEntity.setReqMc(mc);

		AadharDetailsResponseVO aadharDetailsResponseVO = null;
		try
		{
			AadharServiceResponseModel arm = this.aadhaTCSService.AADHAAR_EKYC_FINGER_AUTHENTICATION(endpoint, uid, tid, udc, rdsId, ip, srt, crt, skey, pid, hmac, ci, bt, pincode, version, scheme, department, service, dpId, rdsVer, consentme, attemptType, dc, mi, mc);
			if (!Objects.isNull(arm))
			{
				aadharDetailsResponseVO = new AadharDetailsResponseVO();
				if ((null != arm.getUid()) && (!arm.getUid().trim().isEmpty()))
				{
					arm.setResponseType(ResponseStatus.TCSAadhaarResponseType.SUCCESS);
					aadharDetailsResponseVO.setKSA_KUA_Txn("-");
					aadharDetailsResponseVO.setAuth_date("-");
					aadharDetailsResponseVO.setAuth_status(arm.getResponseType().getLabel());
					aadharDetailsResponseVO.setAuth_transaction_code("-");
					aadharDetailsResponseVO.setCo(arm.getCo());
					aadharDetailsResponseVO.setDistrict(arm.getDist());
					aadharDetailsResponseVO.setDistrict_name(arm.getDist());
					aadharDetailsResponseVO.setDob(arm.getDob());
					aadharDetailsResponseVO.setGender(arm.getGender());
					aadharDetailsResponseVO.setHouse(arm.getHouse());
					aadharDetailsResponseVO.setLc(arm.getLoc());
					aadharDetailsResponseVO.setMandal(arm.getSubdist());
					aadharDetailsResponseVO.setMandal_name(arm.getSubdist());
					aadharDetailsResponseVO.setName(arm.getName());
					aadharDetailsResponseVO.setPincode(arm.getPc());
					aadharDetailsResponseVO.setPo(arm.getPo());
					aadharDetailsResponseVO.setStatecode(arm.getState());
					aadharDetailsResponseVO.setStreet(arm.getStreet());
					aadharDetailsResponseVO.setSubdist(arm.getSubdist());
					aadharDetailsResponseVO.setBase64file(arm.getPhoto());
					log.info("Uid number from Aadhaar:" + arm.getUid());
					aadharDetailsResponseVO.setUid(Long.valueOf(arm.getUid()));
					aadharDetailsResponseVO.setVillage(arm.getVtc());
					aadharDetailsResponseVO.setVillage_name(arm.getVtc());
					aadharDetailsResponseVO.setVtc(arm.getVtc());
					aadharDetailsResponseVO.setPhone(arm.getPhone());
					/*aadhaarLogModel.setRespAuthStatus("SUCCESS");
					aadhaarLogModel.setRespName(aadharDetails.getName());
					aadhaarLogModel.setRespStatecode(aadharDetails.getState());
					aadhaarLogModel.setRespMandal(aadharDetails.getSubdist());*/
				}
				else
				{
					aadharDetailsResponseVO.setAuth_status(ResponseStatus.TCSAadhaarResponseType.FAILD.getLabel());
					aadharDetailsResponseVO.setAuth_err_code(arm.getMsg());
				}

				saveAadhaarTransactions(aadhaarRequestLogEntity,arm.getResponseDesc());
			}
		}
		catch (Exception e)
		{
			/*aadhaarLogModel.setRespAuthStatus("FAILED");
			aadhaarLogModel.setRespAuthErrorCode(e.getMessage());*/
			saveAadhaarTransactions(aadhaarRequestLogEntity,e.getMessage());
			log.error("Exception in AADHAAR_EKYC_FINGER_AUTHENTICATION {}", e);
		}
		//String responseTime = DateUtil.getDate("MM/dd/yyyy hh:mm:ss.SSS a", "IST", new Date());
		//aadhaarLogModel.setRespTime(responseTime);


		return aadharDetailsResponseVO;
	}

	private void saveAadhaarTransactions(AadhaarRequestLogEntity aadhaarRequestLogEntity, String responseDesc) {
		
		AadhaarTransactionDTO aadhaarTransactionDTO=new AadhaarTransactionDTO();
		try {
			aadhaarTransactionDTO.setAadhaarRequest(aadhaarRequestLogEntity);
			aadhaarTransactionDTO.setAadhaarResponse(responseDesc);
			//aadhaarTransactionDTO = aadhaarTransactionDAO.save(aadhaarTransactionDTO);
			log.info("Save Success : AadharNo : {} , id : {}", aadhaarTransactionDTO.getAadhaarRequest().getReqUid(),aadhaarTransactionDTO.getId());
			
		}catch (Exception e) {
			log.error("Save Failed : {} , Exception : {}, Cause : {}", aadhaarTransactionDTO.getAadhaarRequest().getReqUid(),e.getMessage(), e.getCause().getMessage());
		}
	}

	@Override
	public AadharDetailsResponseVO getAadharDetails(Long paramLong) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



}
