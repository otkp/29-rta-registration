package org.epragati.cfms.util;

import org.epragati.cfms.vo.CFMSPaymentReqParams;

import com.fasterxml.jackson.databind.ObjectMapper;


public class CFMSTest {

	public static void rr(String[] args) {
		
		
		StringBuffer sbJson = new StringBuffer();
		
		/*sbJson.append("{");
		sbJson.append("\"headOfAccount\": \"004100101000400000VN\",");
		sbJson.append("\"dDoCode\": \"10012603001\",");
		sbJson.append("\"serviceCode\": \"6045\",");
		sbJson.append("\"deptHOAAmount\": \"200\"}");*/


		
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
		
	    System.out.println("sbJson.toString()"+jsonData);
		ObjectMapper mapper = new ObjectMapper();
		
		try{
			System.out.println("testing..start");
			//CFMSChallan map = mapper.readValue(sbJson.toString(),CFMSChallan.class);
			CFMSPaymentReqParams cFMSPaymentReqParams = mapper.readValue(jsonData, CFMSPaymentReqParams.class);
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
			
			
			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = mapperObj.writeValueAsString(cFMSPaymentReqParams);
            System.out.println("JSON .....    "+jsonStr);
			
		}catch(Exception exp){
			
			exp.printStackTrace();
			
		}
		
	}

}
