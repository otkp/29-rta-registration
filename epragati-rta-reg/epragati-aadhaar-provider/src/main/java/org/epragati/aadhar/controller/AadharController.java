package org.epragati.aadhar.controller;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import org.epragati.aadhaar.AadhaarDetailsRequestVO;
import org.epragati.aadhaar.AadharDetailsResponseVO;
import org.epragati.aadhar.APIResponse;
import org.epragati.aadhar.DataConverter;
import org.epragati.aadhar.DateUtil;
import org.epragati.aadhar.MacIdUtil;
import org.epragati.aadhar.PojoValidatorUtil;
import org.epragati.aadhar.service.AadharInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javassist.NotFoundException;

/**
 * 
 * @author naga.pulaparthi
 *
 */
@CrossOrigin
@RestController
public class AadharController {

	@Value("${rta.aadhar.service.endpoint}")
	private String endpointUrl;

	@Value("${rta.aadhar.service.certificateexpiry}")
	private String certificateExpiry4TCS;

	@Value("${rta.aadhar.service.biometrictype}")
	private String biometricType4TCS;

	@Value("${rta.aadhar.service.version}")
	private String version4TCS;

	@Value("${rta.aadhar.service.departement}")
	private String departement;

	@Value("${rta.aadhar.service.scheme}")
	private String scheme;

	@Value("${rta.aadhar.service.service}")
	private String service4TCS;

	@Value("${isForUnitTest}")
	private boolean isForQA;

	@Value("${rta.aadhar.service.token}")
	private String token;

	@Autowired
	private PojoValidatorUtil pojoValidatorUtil;

	@Autowired
	private AadharInfoService aadharTCSInfoService;

	@Autowired
	private MacIdUtil macIdUtil;

	private static final Logger logger = LoggerFactory.getLogger(AadharController.class);

	@RequestMapping(path = "/getIp", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public APIResponse<?> getIP(HttpServletRequest request) {

		String remoteAddr = "";
		Map<String, String> result = new HashMap<>();

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		result.put("remoteAddr-1", remoteAddr);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			result.put(key, value);
		}

		return new APIResponse<Map<String, String>>(true, HttpStatus.OK, result);
	}

	@RequestMapping(path = "/getAadhaarDetails", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public APIResponse<?> PIDBlockTCS(@RequestHeader(value = "Authorization", required = false) String inputToken,
			@RequestBody AadhaarDetailsRequestVO aadhaarDetailsRequestVO) {
		if (inputToken == null || !inputToken.equals(this.token)) {
			return new APIResponse<String>("Un authorized request, token Requirde", null);
		}
		AadharDetailsResponseVO aadharDetailsResponseVO = null;
		try {
			Optional<String> macIdOptional = macIdUtil.getMacId(isForQA);

			if (!macIdOptional.isPresent()) {
				logger.warn("macid value is empty");
				return new APIResponse<String>(HttpStatus.ACCEPTED, "Unable to getting macid value is empty.");
			}
			aadhaarDetailsRequestVO.setPincode("500072");// any pin number is required.

			aadhaarDetailsRequestVO.setUdc(macIdOptional.get());// Aadhaar number
			// requestModel.setTid("");
			/*
			 * requestModel.setFdc("NA"); // fixed value requestModel.setConsentme("Y");//
			 * fixed value requestModel.setConsent("Y");// fixed value
			 */
			if (isForQA) {
				// Returning Dummy aadhar model
				logger.info("Testing result we will send");
				try {

					aadharDetailsResponseVO = (AadharDetailsResponseVO) DataConverter.jsonFileToObject(null,
							AadharDetailsResponseVO.class, aadhaarDetailsRequestVO.getUid_num());
					aadharDetailsResponseVO.setUid(Long.parseLong(aadhaarDetailsRequestVO.getUid_num()));

				} catch (FileNotFoundException f) {
					logger.warn("Loading default aadhar details( Test ) .");
					aadharDetailsResponseVO = (AadharDetailsResponseVO) DataConverter.jsonFileToObject(null,
							AadharDetailsResponseVO.class, "1");
					aadharDetailsResponseVO.setUid(Long.parseLong(aadhaarDetailsRequestVO.getUid_num()));
				}
			} else {
				List<FieldError> fieldErrors = pojoValidatorUtil.doValidation(aadhaarDetailsRequestVO,
						aadhaarDetailsRequestVO.getClass().getName());

				if (!fieldErrors.isEmpty()) {
					return new APIResponse<String>("Validation failed", fieldErrors);
				}
				logger.info("Actual result we will send");
				aadharDetailsResponseVO = validateAadhar(token, aadhaarDetailsRequestVO);
			}

		} catch (ServiceException e) {
			logger.error("ServiceException from Aadhar ");
			return new APIResponse<String>("ServiceException from Aadhar ", null);
		} catch (RemoteException e) {
			logger.error("Remote error occured while accessing Aadhar Service {}", e.getMessage());
			return new APIResponse<String>(HttpStatus.ACCEPTED, "Remote error occured while accessing Aadhar Service");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException while calling Aadhar {}", e.getMessage());
			return new APIResponse<String>(HttpStatus.ACCEPTED, "FileNotFoundException while calling Aadhar");
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncoding found while calling Aadhar service {}", e.getMessage());
			return new APIResponse<String>(HttpStatus.ACCEPTED,
					"UnsupportedEncoding found while calling Aadhar service");
		} catch (NotFoundException e) {
			logger.error("Not recieved any data from Aadhar {}", e.getMessage());
			return new APIResponse<String>(HttpStatus.ACCEPTED, "Not recieved any data from Aadhar");
		} catch (Exception ex) {
			logger.error("Exception [{}] ", ex);
			return new APIResponse<String>(HttpStatus.ACCEPTED, ex.getMessage());
		}
		logger.info("aadhaar success");
		logger.debug("aadhaar responce success:  {}", aadharDetailsResponseVO);

		return new APIResponse<AadharDetailsResponseVO>(true, HttpStatus.OK, aadharDetailsResponseVO);
	}

	/**
	 * validateAadhar for prepare request for aadhar validation and returns aadhar
	 * details from service
	 */

	private AadharDetailsResponseVO validateAadhar(String token, AadhaarDetailsRequestVO aadhaarDetailsRequestVO)
			throws NotFoundException, ServiceException, RemoteException, FileNotFoundException,
			UnsupportedEncodingException, javax.xml.rpc.ServiceException {
		AadharDetailsResponseVO aadharDetailsResponseVO = null;
		String ip = "NA"; // fix value
		String deviceName = "computer"; // fix value

		String dealerPinCode = aadhaarDetailsRequestVO.getPincode();

		String srt = DateUtil.getDate("MM/dd/yyyy hh:mm:ss a", "IST", new Date()); // "9/16/2016
		// 12:52:15
		// AM"
		/*
		 * logger.
		 * info("For Aadhar: Server time stamp: {} and Client request time: {requestModel.getCrt()}"
		 * , srt, requestModel.getCrt());
		 */
		URL endpoint = getEndPointURL(endpointUrl);
		logger.debug(" ****** GENERATED  ENDPOINT url:{endpoint.toString()}", endpoint.toString());
		aadharDetailsResponseVO = this.aadharTCSInfoService.AADHAAR_EKYC_FINGER_AUTHENTICATION(endpoint, aadhaarDetailsRequestVO.getUid_num(),
				aadhaarDetailsRequestVO.getTid(), aadhaarDetailsRequestVO.getUdc(), aadhaarDetailsRequestVO.getRdsId(), ip, srt, aadhaarDetailsRequestVO.getCrt(),
				aadhaarDetailsRequestVO.getEncSessionKey(), aadhaarDetailsRequestVO.getEncryptedPid(), aadhaarDetailsRequestVO.getEncHmac(),
				this.certificateExpiry4TCS, this.biometricType4TCS, dealerPinCode, this.version4TCS, this.scheme,
				this.departement, this.service4TCS, aadhaarDetailsRequestVO.getDpId(), aadhaarDetailsRequestVO.getRdsVer(),
				aadhaarDetailsRequestVO.getConsentme(), "KELL", deviceName, aadhaarDetailsRequestVO.getAttemptType(), aadhaarDetailsRequestVO.getDc(),
				aadhaarDetailsRequestVO.getMi(), aadhaarDetailsRequestVO.getMc());
		if (null == aadharDetailsResponseVO) {
			logger.error("********Received null model from TCS  Aadhaar");
			throw new NotFoundException("No Data Received From Aadhar !!!");
		}
		if ("FAILED".equalsIgnoreCase(aadharDetailsResponseVO.getAuth_status())) {
			return aadharDetailsResponseVO;
		}
		return aadharDetailsResponseVO;
	}

	/**
	 * @param macIdUtil
	 *            the macIdUtil to set
	 */
	public void setMacIdUtil(MacIdUtil macIdUtil) {
		this.macIdUtil = macIdUtil;
	}

	private URL getEndPointURL(String url) {
		logger.debug(" Provided ekyc url:{}", url);
		URL endpoint = null;
		try {
			endpoint = new URL(url);
		} catch (java.net.MalformedURLException e) {
			logger.info(" ERROR can not create ENDPOINT URL from provided ekyc url:{}. Error:{}", url, e.getMessage());
		}
		return endpoint;
	}

	/**
	 * @param aadharTCSInfoService
	 *            the aadharTCSInfoService to set
	 */
	public void setAadharTCSInfoService(AadharInfoService aadharTCSInfoService) {
		this.aadharTCSInfoService = aadharTCSInfoService;
	}

	/**
	 * @return the endpointUrl
	 */
	public String getEndpointUrl() {
		return endpointUrl;
	}

	/**
	 * @param endpointUrl
	 *            the endpointUrl to set
	 */
	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	/**
	 * @return the certificateExpiry4TCS
	 */
	public String getCertificateExpiry4TCS() {
		return certificateExpiry4TCS;
	}

	/**
	 * @param certificateExpiry4TCS
	 *            the certificateExpiry4TCS to set
	 */
	public void setCertificateExpiry4TCS(String certificateExpiry4TCS) {
		this.certificateExpiry4TCS = certificateExpiry4TCS;
	}

	/**
	 * @return the biometricType4TCS
	 */
	public String getBiometricType4TCS() {
		return biometricType4TCS;
	}

	/**
	 * @param biometricType4TCS
	 *            the biometricType4TCS to set
	 */
	public void setBiometricType4TCS(String biometricType4TCS) {
		this.biometricType4TCS = biometricType4TCS;
	}

	/**
	 * @return the version4TCS
	 */
	public String getVersion4TCS() {
		return version4TCS;
	}

	/**
	 * @param version4tcs
	 *            the version4TCS to set
	 */
	public void setVersion4TCS(String version4tcs) {
		version4TCS = version4tcs;
	}

	/**
	 * @return the departement
	 */
	public String getDepartement() {
		return departement;
	}

	/**
	 * @param departement
	 *            the departement to set
	 */
	public void setDepartement(String departement) {
		this.departement = departement;
	}

	/**
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @param scheme
	 *            the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	/**
	 * @return the service4TCS
	 */
	public String getService4TCS() {
		return service4TCS;
	}

	/**
	 * @param service4tcs
	 *            the service4TCS to set
	 */
	public void setService4TCS(String service4tcs) {
		service4TCS = service4tcs;
	}

	/**
	 * @return the isForQA
	 */
	public boolean isForQA() {
		return isForQA;
	}

	/**
	 * @param isForQA
	 *            the isForQA to set
	 */
	public void setForQA(boolean isForQA) {
		this.isForQA = isForQA;
	}

	/**
	 * @return the aadharTCSInfoService
	 */
	public AadharInfoService getAadharTCSInfoService() {
		return aadharTCSInfoService;
	}

	/**
	 * @return the macIdUtil
	 */
	public MacIdUtil getMacIdUtil() {
		return macIdUtil;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the pojoValidatorUtil
	 */
	public PojoValidatorUtil getPojoValidatorUtil() {
		return pojoValidatorUtil;
	}

	/**
	 * @param pojoValidatorUtil
	 *            the pojoValidatorUtil to set
	 */
	public void setPojoValidatorUtil(PojoValidatorUtil pojoValidatorUtil) {
		this.pojoValidatorUtil = pojoValidatorUtil;
	}

	/*
	 * 
	 * @RequestMapping(path={"/PIDBlockTCS"},
	 * method={org.springframework.web.bind.annotation.RequestMethod.POST},
	 * produces={"application/json", "application/xml"}) public APIResponse<?>
	 * PIDBlockTCS(@RequestHeader("Authorization") String token, @Valid @RequestBody
	 * AadhaarDetailsRequestModel requestModel) { AadharUserDetailsResponseModel
	 * aadharModel = null; try { aadharModel = aadharTCS(token, requestModel); }
	 * catch (org.hibernate.service.spi.ServiceException e) {
	 * logger.error("ServiceException from Aadhar!!!");
	 * errorResponse.setMessage("ServiceException from Aadhar!!!"); return
	 * ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); } catch
	 * (RemoteException e) {
	 * logger.error("Remote error occured while accessing Aadhar Service !!!");
	 * errorResponse.
	 * setMessage("Remote error occured while accessing Aadhar Service !!!"); return
	 * ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); } catch
	 * (FileNotFoundException e) {
	 * logger.error("FileNotFoundException while calling Aadhar!!!");
	 * errorResponse.setMessage("Some error occured while Aadhar Service!!!");
	 * return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); }
	 * catch (UnsupportedEncodingException e) {
	 * logger.error("UnsupportedEncoding found while calling Aadhar service !!!");
	 * errorResponse.
	 * setMessage("UnsupportedEncoding found while calling Aadhar service !!!");
	 * return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); }
	 * catch (NotFoundException e) {
	 * logger.error("Not recieved any data from Aadhar !!!");
	 * errorResponse.setMessage("Not recieved any data from Aadhar !!!"); return
	 * ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); } catch
	 * (javax.xml.rpc.ServiceException e) {
	 * logger.error("RPC call error Aadhar Service!!!");
	 * errorResponse.setMessage("RPC call error Aadhar Service!!!"); return
	 * ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); } catch
	 * (Exception ex) { logger.error("Exception :: " + ex.getMessage());
	 * errorResponse.setMessage(ex.getMessage()); return
	 * ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse); } return
	 * ResponseEntity.status(HttpStatus.OK).body(aadharModel); }
	 * 
	 * private AadharUserDetailsResponseModel aadharTCS(String token,
	 * AadhaarDetailsRequestModel requestModel) throws NotFoundException,
	 * org.hibernate.service.spi.ServiceException, RemoteException,
	 * FileNotFoundException, UnsupportedEncodingException,
	 * javax.xml.rpc.ServiceException { AadharUserDetailsResponseModel aadharModel =
	 * null; String ip = "NA";
	 * 
	 * String deviceName = "computer";
	 * 
	 * String dealerPinCode = requestModel.getPincode();
	 * 
	 * String srt = DateUtil.getDate("MM/dd/yyyy hh:mm:ss a", "IST", new Date());
	 * logger.info("For Aadhar: Server time stamp:" + srt +
	 * " and Client request time:" + requestModel.getCrt());
	 * 
	 * URL endpoint = getEndPointURL(this.endpointUrl); //endpoint =
	 * getEndPointURL(EKYC_SOAP_ADDRESS);
	 * 
	 * logger.debug(" ****** GENERATED  ENDPOINT url:" + endpoint.toString());
	 * aadharModel =
	 * this.aadharTCSInfoService.AADHAAR_EKYC_FINGER_AUTHENTICATION(endpoint,
	 * requestModel.getUid_num(), requestModel.getTid(), requestModel.getUdc(),
	 * requestModel.getRdsId(), ip, srt, requestModel .getCrt(),
	 * requestModel.getEncSessionKey(), requestModel.getEncryptedPid(), requestModel
	 * .getEncHmac(), this.certificateExpiry4TCS, this.biometricType4TCS,
	 * dealerPinCode, this.version4TCS, this.scheme, this.departement,
	 * this.service4TCS, requestModel .getDpId(), requestModel.getRdsVer(),
	 * requestModel.getConsentme(), userName, deviceName,
	 * requestModel.getAttemptType(), requestModel .getDc(), requestModel.getMi(),
	 * requestModel.getMc()); if (ObjectsUtil.isNull(aadharModel)) {
	 * logger.error("********Received null model from TCS  Aadhaar"); throw new
	 * NotFoundException("No Data Received From Aadhar !!!"); } if
	 * ("FAILED".equalsIgnoreCase(aadharModel.getAuth_status())) { return
	 * aadharModel; } logger.info(" **** Saving aadhaar model ****");
	 * logger.info(" **** Aadhar data saved successfully ****");
	 * 
	 * return aadharModel; }
	 */

}
