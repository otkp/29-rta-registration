package org.epragati.cfms.payment.service;

import org.epragati.cfms.vo.CFMSPaymentReqParams;

public interface CFMSPaymentGatewayService {

	CFMSPaymentReqParams prepareRequestObject(String reqData);
	String prepareRedirectUrl(String reqData);
}
