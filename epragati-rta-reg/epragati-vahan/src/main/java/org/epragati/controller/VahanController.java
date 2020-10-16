package org.epragati.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.epragati.exception.BadRequestException;
import org.epragati.util.AppMessages;
import org.epragati.util.GateWayResponse;
import org.epragati.vahan.VahanResponseModel;
import org.epragati.vahan.exception.IllegalEngineNumberException;
import org.epragati.vahan.registered.VahanClientImpl;
import org.epragati.vahan.registered.model.RegisteredVehicleDetails;
import org.epragati.vahan.registered.service.VahanRegisteredService;
import org.epragati.vahan.registered.vo.VahanVehicleDetailsVO;
import org.epragati.vahan.unregistered.UnregisteredVahanClientImpl;
import org.epragati.vahan.unregistered.model.UnregisteredChassisInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class VahanController {
	
	@Autowired
	private AppMessages appMsg;

	@Value("${vahan.userId}")
	private String vahanUserId;

	@Value("${vahan.password}")
	private String vahanpassword;

	@Autowired
	private VahanRegisteredService vahanRegisteredService;
	
	private static final Logger logger = Logger.getLogger(VahanController.class);
	
	@RequestMapping(method = { RequestMethod.POST } , path = "getVahanDetails")
	public GateWayResponse<?> fetchVahanDetails(
			@RequestParam(name="engineNo", required=true) String engineNo,
			@RequestParam(name="chasisNo", required=true) String chasisNo
			){

		VahanResponseModel<UnregisteredChassisInfo> chasisInfo = null;
		logger.info("before UnregisteredVahanClientImpl.");
		UnregisteredVahanClientImpl impl = new UnregisteredVahanClientImpl(vahanUserId, vahanpassword);
		logger.info("before UnregisteredVahanClientImpl.");
		try {
			logger.info("before getChassisInfo.");
			chasisInfo = impl.getChassisInfo(chasisNo, engineNo);
			logger.info("after getChassisInfo.");
		} catch (IllegalEngineNumberException e) {
			logger.info("getVehicleDetails IllegalEngineNumberException:{}");
			return new GateWayResponse<String>(true, HttpStatus.SERVICE_UNAVAILABLE,e.getLocalizedMessage());
		}

		return new GateWayResponse<VahanResponseModel<UnregisteredChassisInfo>>(true, HttpStatus.OK,chasisInfo);
	}
	
	//remove after testing
	
	
		@PostMapping(value = "getDetails",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
		public GateWayResponse<?> fetchVahanVehicleDetails(@RequestParam(name="prNo", required=true) String prNo){
			//String certificatesTrustStorePath = "C:\\Program Files\\Java\\jdk1.8.0_111\\jre\\lib\\security\\cacerts";
			//System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
			VahanResponseModel<RegisteredVehicleDetails> vahaninfo = null;

	        VahanClientImpl impl = new VahanClientImpl("aPwsDL@001");
	        try {
	        	 vahaninfo = impl.getDetails("AP001", prNo);
			}
	        catch (Exception e) {
				return new GateWayResponse<String>(true, HttpStatus.SERVICE_UNAVAILABLE,e.getLocalizedMessage());
			}
	        return new GateWayResponse<VahanResponseModel<RegisteredVehicleDetails>>(true, HttpStatus.OK,vahaninfo);
		}
		
		@GetMapping(value = "getVehicleDetails",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
		public GateWayResponse<?> vahanBasedOnPrNo(@RequestParam(name="prNo", required=true) String prNo){
			VahanVehicleDetailsVO registeredVehicleVO = null;
			if(StringUtils.isEmpty(prNo)){
				return new GateWayResponse<String>(true, HttpStatus.SERVICE_UNAVAILABLE,"PrNo Not Empty");
			}
	        try {
	        	registeredVehicleVO =vahanRegisteredService.vahanVehicleBasedOnPrNo(prNo);
			}
	        catch (BadRequestException bex) {
	        	logger.error("getVehicleDetails BadRequestException:{}", bex);
				return new GateWayResponse<String>(HttpStatus.BAD_REQUEST,bex.getMessage());
			}
	        catch (Exception e) {
	        	logger.error("getVehicleDetails Exception:{}", e);
				return new GateWayResponse<String>(HttpStatus.SERVICE_UNAVAILABLE,e.getMessage());
			}
	        return new GateWayResponse<VahanVehicleDetailsVO>(true, HttpStatus.OK,registeredVehicleVO);
		}
		
}
