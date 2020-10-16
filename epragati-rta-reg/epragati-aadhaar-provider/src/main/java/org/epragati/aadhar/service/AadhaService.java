package org.epragati.aadhar.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.epragati.aadhaar.AadharServiceResponseModel;


/**
 * AadharService provides details for aadhar
 * 
 * @author naga.pulaparthi
 *
 */
public interface AadhaService {

	public abstract AadharServiceResponseModel AADHAAR_EKYC_FINGER_AUTHENTICATION(URL paramURL, String paramString1,
			String paramString2, String paramString3, String paramString4, String paramString5, String paramString6,
			String paramString7, String paramString8, String paramString9, String paramString10, String paramString11,
			String paramString12, String paramString13, String paramString14, String paramString15,
			String paramString16, String paramString17, String paramString18, String paramString19,
			String paramString20, String paramString21, String paramString22, String paramString23,
			String paramString24) throws RemoteException, FileNotFoundException, UnsupportedEncodingException,
			ServiceException, MalformedURLException;

}
