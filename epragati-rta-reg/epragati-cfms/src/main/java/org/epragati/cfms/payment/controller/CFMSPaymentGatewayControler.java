package org.epragati.cfms.payment.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.epragati.cfms.payment.service.CFMSPaymentGatewayService;
import org.epragati.cfms.util.GateWayResponse;
import org.epragati.cfms.vo.CFMSPaymentReqParams;
import org.epragati.cfms.vo.DataVO;
import org.epragati.cfms.vo.ServiceInfoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@RestController
public class CFMSPaymentGatewayControler {

	private static final Logger logger = LoggerFactory.getLogger(CFMSPaymentGatewayControler.class);

	
	@Autowired
	private CFMSPaymentGatewayService cFMSPaymentGatewayService;
	
	//@Autowired
	//private AppMessages appMessages;
	
	
	@GetMapping(value = "getCFMSRequest", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> getCFMSRequest(@RequestParam(name = "paydata") String paydata) throws IOException {

		logger.info("doCFMSDataPost Start");
		try {

			System.out.println("Request data is... "+paydata);
			CFMSPaymentReqParams result = cFMSPaymentGatewayService.prepareRequestObject(paydata);
			
			System.out.println("Request data is"+result.toString());

			return new GateWayResponse<CFMSPaymentReqParams>(HttpStatus.OK, result,"Success");
			
					//appMessages.getResponseMessage(MessageKeys.MESSAGE_SUCCESS));
		} catch (Exception e) {
			logger.error("Exception while getCFMSPayRequest processing the  payment :{e}", e);
			 e.printStackTrace();
		}
		logger.info("doCFMSDataPost End");
		return new GateWayResponse<>(HttpStatus.OK, "","RecordNotfound");
				//appMessages.getResponseMessage(MessageKeys.MESSAGE_NO_RECORD_FOUND));	
	}	


//	@RequestMapping(method = { RequestMethod.POST } , path = "getCFMSPayRequest")
//	public GateWayResponse<?> getCFMSPayRequest(@RequestParam(name = "paydata") String paydata) throws IOException {
//
//		logger.info("doCFMSDataPost Start");
//		try {
//
//			System.out.println("Request data is... "+paydata);
//			CFMSPaymentReqParams result = cFMSPaymentGatewayService.prepareRequestObject(paydata);
//			
//			System.out.println("Request data is"+result.toString());
//
//			return new GateWayResponse<CFMSPaymentReqParams>(HttpStatus.OK, result,"Success");
//			
//					//appMessages.getResponseMessage(MessageKeys.MESSAGE_SUCCESS));
//		} catch (Exception e) {
//			logger.error("Exception while getCFMSPayRequest processing the  payment :{e}", e);
//			 e.printStackTrace();
//		}
//		logger.info("doCFMSDataPost End");
//		return new GateWayResponse<>(HttpStatus.OK, "","RecordNotfound");
//				//appMessages.getResponseMessage(MessageKeys.MESSAGE_NO_RECORD_FOUND));	
//	}


	
	@PostMapping(path = "getCFMSPayRequest")
	public GateWayResponse<?> getCFMSPayRequest(@RequestBody DataVO vo) throws IOException {
	//	public GateWayResponse<?> getCFMSPayRequest(@RequestParam(name = "paydata") String paydata) throws IOException {

		String paydata = vo.getPaydata();
		System.out.println("doCFMSDataPost Start " +vo.getDeptCode());
			
		//System.out.println("doCFMSDataPost Start " +paydata);
	logger.info("doCFMSDataPost Start");
	try {

		System.out.println("Request data is... "+paydata);
		CFMSPaymentReqParams result = cFMSPaymentGatewayService.prepareRequestObject(paydata);
		
		System.out.println("Request data is"+result.toString());

		return new GateWayResponse<CFMSPaymentReqParams>(HttpStatus.OK, result,"Success");
		
				//appMessages.getResponseMessage(MessageKeys.MESSAGE_SUCCESS));
	} catch (Exception e) {
		logger.error("Exception while getCFMSPayRequest processing the  payment :{e}", e);
		 e.printStackTrace();
	}
	logger.info("doCFMSDataPost End");
	return new GateWayResponse<>(HttpStatus.OK, "","RecordNotfound");
			//appMessages.getResponseMessage(MessageKeys.MESSAGE_NO_RECORD_FOUND));	
	}

	
	@PostMapping(path = "/getCFMSRedirectURL", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public void doCFMSDataPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// logger.info("map data : "+map);
		logger.info("doCFMSDataPost Start");
		
		try {
			HttpSession hsession = request.getSession();
			String payDetails = request.getParameter("paydata");
			System.out.println("Request data is"+payDetails);
			String cfmsRequestUrl = cFMSPaymentGatewayService.prepareRedirectUrl(payDetails);			
			System.out.println("cfmsRequestUrl data is"+cfmsRequestUrl);
			response.sendRedirect(cfmsRequestUrl);
		} catch (Exception e) {
			logger.error("Exception while doCFMSDataPost the  payment :{e}", e);
		    e.printStackTrace();
			response.sendRedirect("ERRORPage");
			
		}
		logger.info("doCFMSDataPost End");
		//return "Success";
	}
	
	
	@PostMapping(path = "/serviceInfo", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public GateWayResponse<?> doServiceInfo(@RequestBody ServiceInfoData serviceInfoData) throws IOException {
		logger.info("doServiceInfo Start");
		try {
			System.out.println("serviceInfoData .... "+serviceInfoData.toString());
			CFMSPaymentReqParams result = cFMSPaymentGatewayService.prepareRequestObject(serviceInfoData.getServiceData());
			System.out.println("Request data is"+result.toString());
			return new GateWayResponse<CFMSPaymentReqParams>(HttpStatus.OK, result,"Success");
		} catch (Exception e) {
			logger.error("Exception while getCFMSPayRequest processing the  payment :{e}", e);
			 e.printStackTrace();
		}
		logger.info("doServiceInfo End");
		return new GateWayResponse<>(HttpStatus.OK, "","RecordNotfound");
				//appMessages.getResponseMessage(MessageKeys.MESSAGE_NO_RECORD_FOUND));	
	}	
	
	
	
	@GetMapping(path = "/payment", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ModelAndView doRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// logger.info("map data : "+map);
		logger.info("doRedirect Start");
		String redURL = "https://qascfms.apcfss.in:44300/sap/bc/bsp/sap/zcfms_line/index.htm?DC=TRB03&DTId=112234&RN=RTAINTEGRATION&RID=RTA&TA=300&Ch=004100101000400000VN,10012603001,6045,200&Oth=01022308001,100&RUrl=http://localhost:8080/RTAPaymentSuccess.html";
		try {

			System.out.println("The data is calling "+redURL );
		} catch (Exception e) {
			logger.error("Exception while doRedirect the  payment :{e}", e);
		    e.printStackTrace();

			
		}
		logger.info("doRedirect End");
		return new ModelAndView("redirect:"+redURL);
	}	

}
