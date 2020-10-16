package org.epragati.aadhar.service;

/**
 * @author naga.pulaparthi
*/

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.RemoteException;

import org.epragati.aadhaar.AadharDetailsResponseVO;


public interface AadharInfoService {

	AadharDetailsResponseVO AADHAAR_EKYC_FINGER_AUTHENTICATION(URL paramURL, String paramString1,
			String paramString2, String paramString3, String paramString4, String paramString5, String paramString6,
			String paramString7, String paramString8, String paramString9, String paramString10, String paramString11,
			String paramString12, String paramString13, String paramString14, String paramString15,
			String paramString16, String paramString17, String paramString18, String paramString19,
			String paramString20, String paramString21, String paramString22, String paramString23,
			String paramString24, String paramString25, String paramString26)
			throws RemoteException, FileNotFoundException, UnsupportedEncodingException,
			org.hibernate.service.spi.ServiceException, javax.xml.rpc.ServiceException;

	AadharDetailsResponseVO getAadharDetails(Long paramLong) throws Exception;
}
