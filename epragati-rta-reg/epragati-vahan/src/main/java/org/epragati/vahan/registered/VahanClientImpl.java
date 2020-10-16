package org.epragati.vahan.registered;

import java.io.StringReader;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.epragati.vahan.ResponseType;
import org.epragati.vahan.StatusMessage;
import org.epragati.vahan.VahanResponseModel;
import org.epragati.vahan.registered.model.RegisteredVahanResponseModel;
import org.epragati.vahan.registered.model.RegisteredVehicleDetails;
import org.epragati.vahan.util.CryptographyAES;

import nic.vahan.web.server.VahanInfo;
import nic.vahan.web.server.VahanInfo_Service;

public class VahanClientImpl
  implements VahanClient
{
  private static final Logger logger = Logger.getLogger(VahanClientImpl.class);
  private VahanInfo vahanInfo;
  private CryptographyAES crypt;
  
  public VahanClientImpl(String secretKey)
  {
    this.vahanInfo = new VahanInfo_Service().getVahanInfoPort();
    this.crypt = new CryptographyAES(secretKey);
  }	
  
  public VahanResponseModel<RegisteredVehicleDetails> getDetails(String clientId, String regnNo)
  {
    if ((Objects.isNull(clientId)) || (Objects.isNull(regnNo))) {
      throw new IllegalArgumentException("clientId and regnNo can't be null");
    }
    return handleResponse(this.vahanInfo.getDetails(clientId, regnNo));
  }
  
  public VahanResponseModel<RegisteredVehicleDetails> getChasisDetails(String clientId, String chasiNo)
  {
    if ((Objects.isNull(clientId)) || (Objects.isNull(chasiNo))) {
      throw new IllegalArgumentException("clientId and chasiNo can't be null");
    }
    return handleResponse(this.vahanInfo.getChasisDetails(clientId, chasiNo));
  }
  
  private VahanResponseModel<RegisteredVehicleDetails> handleResponse(String responseString)
  {
    RegisteredVehicleDetails vd = null;
    System.out.println("RES :  "+responseString);
    try
    {
      responseString = decrypt(responseString);
      if (Objects.isNull(responseString)) {
        return new RegisteredVahanResponseModel(ResponseType.ERROR, responseString);
      }
      vd = (RegisteredVehicleDetails)getUnmarshaller(RegisteredVehicleDetails.class).unmarshal(new StringReader(responseString));
      if ((vd.getStatusMessage() != null) && (vd.getStatusMessage().equals(StatusMessage.OK.label()))) {
        return new RegisteredVahanResponseModel(ResponseType.SUCCESS, responseString, vd);
      }
      return new RegisteredVahanResponseModel(ResponseType.NOT_FOUND, responseString);
    }
    catch (JAXBException e)
    {
      logger.debug(e.getMessage());
      return new RegisteredVahanResponseModel(ResponseType.ERROR, responseString);
    }
    catch (IllegalArgumentException e)
    {
      logger.debug(e.getMessage());
    }
    return new RegisteredVahanResponseModel(ResponseType.ERROR, responseString);
  }
  
  private Unmarshaller getUnmarshaller(Class<?> clazz)
    throws JAXBException
  {
    JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { clazz });
    return jaxbContext.createUnmarshaller();
  }
  
  private String decrypt(String text)
  {
    return this.crypt.decryptFile(text);
  }
}
