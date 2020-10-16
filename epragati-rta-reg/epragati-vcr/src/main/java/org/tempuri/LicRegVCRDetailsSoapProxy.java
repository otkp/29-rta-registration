package org.tempuri;

public class LicRegVCRDetailsSoapProxy implements org.tempuri.LicRegVCRDetailsSoap {
  private String _endpoint = null;
  private org.tempuri.LicRegVCRDetailsSoap licRegVCRDetailsSoap = null;
  
  public LicRegVCRDetailsSoapProxy() {
    _initLicRegVCRDetailsSoapProxy();
  }
  
  public LicRegVCRDetailsSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initLicRegVCRDetailsSoapProxy();
  }
  
  private void _initLicRegVCRDetailsSoapProxy() {
    try {
      licRegVCRDetailsSoap = (new org.tempuri.LicRegVCRDetailsLocator()).getLicRegVCRDetailsSoap();
      if (licRegVCRDetailsSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)licRegVCRDetailsSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)licRegVCRDetailsSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (licRegVCRDetailsSoap != null)
      ((javax.xml.rpc.Stub)licRegVCRDetailsSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.LicRegVCRDetailsSoap getLicRegVCRDetailsSoap() {
    if (licRegVCRDetailsSoap == null)
      _initLicRegVCRDetailsSoapProxy();
    return licRegVCRDetailsSoap;
  }
  
  public java.lang.String getLicRegVCRDetails(java.lang.String document_type, java.lang.String licenseNo, java.lang.String regnNo) throws java.rmi.RemoteException{
    if (licRegVCRDetailsSoap == null)
      _initLicRegVCRDetailsSoapProxy();
    return licRegVCRDetailsSoap.getLicRegVCRDetails(document_type, licenseNo, regnNo);
  }
  
  
}