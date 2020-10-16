package org.epragati.cfms.payment.service.impl;

import java.util.ArrayList;

import org.epragati.cfms.payment.service.CFMSPaymentGatewayService;
import org.epragati.cfms.vo.CFMSChallan;
import org.epragati.cfms.vo.CFMSOtherChallan;
import org.epragati.cfms.vo.CFMSPaymentReqParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CFMSPaymentGatewayServiceImpl implements CFMSPaymentGatewayService {

	private static final Logger logger = LoggerFactory.getLogger(CFMSPaymentGatewayServiceImpl.class);

	@Value("${cfms.payment.service.userId:aptransport}")
	private String cfmsUserId;// "; aptransport $transport$17

	@Value("${{cfms.payment.service.pswd:$transport$17}")
	private String cfmsPassword;
	
	@Value("${cfms.redirect.url:cfmsurmissing}")
	private String cfmsRedirectUrl;
	
	@Value("${cfms.request.url:missingurl}")
	private String cfmsRequestURL;


	@Autowired
	private RestTemplate restTemplate;
	
	String COMMA=",";
	


	@Override
	public CFMSPaymentReqParams prepareRequestObject(String reqData) {
		
		logger.info("prepareRequestObject Start");
		CFMSPaymentReqParams processedData = getBeanObject(reqData);
		logger.info("prepareRequestObject End");
		return processedData;
	}
	
	
	@Override
	public String prepareRedirectUrl(String reqData) {
		logger.info("prepareRedirectUrl Start");	
		CFMSPaymentReqParams processedData = getBeanObject(reqData);
		String requestReqParms = prepareRedirectParams(processedData);
		System.out.println("requestUrl"+cfmsRequestURL+"?"+ requestReqParms);
		logger.info("requestUrl"+cfmsRequestURL+"?"+ requestReqParms);
		
		logger.info("prepareRedirectUrl End");	
		return cfmsRequestURL+"?"+ requestReqParms;

	}
		
	
	public String prepareRedirectParams(CFMSPaymentReqParams processedData) {
		
		logger.info("prepareRedirectParams Start");	
		StringBuffer sbRedirectData = new StringBuffer();
		sbRedirectData.append("DC="+processedData.getDeptCode());
		sbRedirectData.append("&DTId="+processedData.getDeptTransactionId());
		sbRedirectData.append("&RN="+processedData.getRemitterName());
		sbRedirectData.append("&RID="+processedData.getDeptRId());
		sbRedirectData.append("&TA="+processedData.getTotalAmount());
		ArrayList<CFMSChallan> chalanList = (ArrayList<CFMSChallan>) processedData.getcFMSChallanList();
		for(CFMSChallan challan : chalanList){
			sbRedirectData.append("&Ch="+challan.getHeadOfAccount() + COMMA + challan.getdDoCode() + COMMA+challan.getServiceCode() + COMMA + challan.getDeptHOAAmount());
		}
		ArrayList<CFMSOtherChallan> otherChallanList = (ArrayList<CFMSOtherChallan>) processedData.getcFMSOtherChallanList();
		for(CFMSOtherChallan otherchallan : otherChallanList){
			sbRedirectData.append("&Oth="+otherchallan.getAccountNo() + COMMA + otherchallan.getAmount());
		}
		sbRedirectData.append("&RUrl="+processedData.getRedirectUrl());
		logger.info("prepareRedirectParams End");	
		return sbRedirectData.toString();
	}	
	
	



	private CFMSPaymentReqParams getBeanObject(String jsonData){
		logger.info("getBeanObject Start");		
		CFMSPaymentReqParams cFMSPaymentReqParams = new CFMSPaymentReqParams();
	
	    System.out.println("sbJson.toString()"+jsonData);
		ObjectMapper mapper = new ObjectMapper();
		
		try{
			System.out.println("testing..start");
			cFMSPaymentReqParams = mapper.readValue(jsonData, CFMSPaymentReqParams.class);
			cFMSPaymentReqParams.setCfmsRequestUrl(cfmsRequestURL);
			System.out.println("testing");
			System.out.println("Dept Code is: "+cFMSPaymentReqParams.getDeptCode());
			System.out.println("Dept TransactionId is: "+cFMSPaymentReqParams.getDeptTransactionId());
			System.out.println("Dept RId is: "+cFMSPaymentReqParams.getDeptRId());
			System.out.println("RemitterName is: "+cFMSPaymentReqParams.getRemitterName());
			System.out.println("User Id is: "+cFMSPaymentReqParams.getUserId());
			System.out.println("Pwd is: "+cFMSPaymentReqParams.getPwd());			
			System.out.println("RedirectUrl is: "+cFMSPaymentReqParams.getRedirectUrl());
			System.out.println("cFMSChallanList is: "+cFMSPaymentReqParams.getcFMSChallanList());
			System.out.println("cFMSOtherChallanList is: "+cFMSPaymentReqParams.getcFMSOtherChallanList());			
		}catch(Exception exp){
			
			exp.printStackTrace();
			
		}
		logger.info("getBeanObject End");	
		return cFMSPaymentReqParams;	
	}
	
	private String prepareTestData(){
		StringBuffer sbJson = new StringBuffer();
		sbJson.append("{");
		sbJson.append("\"deptCode\": \"TRB03\",");
		sbJson.append("\"deptTransactionId\": \"112234\",");
		sbJson.append("\"remitterName\": \"RTAINTEGRATION\",");
		sbJson.append("\"deptRId\": \"RTA\",");
		sbJson.append("\"totalAmount\": \"300\",");
		sbJson.append("\"cFMSChallanList\": [{");
		sbJson.append("\"headOfAccount\": \"004100101000400000VN\",");
		sbJson.append("\"dDoCode\": \"10012603001\",");
		sbJson.append("\"serviceCode\": \"6045\",");
		sbJson.append("\"deptHOAAmount\": \"200\"");
		sbJson.append("}],");
		sbJson.append("\"cFMSOtherChallanList\": [{");
		sbJson.append("\"accountNo\": \"01022308001\",");
		sbJson.append("\"amount\": \"100\"");
		sbJson.append("}],");
		sbJson.append("\"redirectUrl\": \"http://localhost:8080/RTAPaymentSuccess.html\",");
		sbJson.append("\"userId\": \"aptransport\",");
		sbJson.append("\"pwd\": \"$transport$17\"");
		sbJson.append("}");		

		String jsonData  =  sbJson.toString();
		return jsonData;
	}	
	
}
