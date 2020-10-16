package org.tempuri;

public abstract class EKYCSoapProxy implements org.tempuri.EKYCSoap {
  private String _endpoint = null;
  private org.tempuri.EKYCSoap eKYCSoap = null;
  
  public EKYCSoapProxy() {
    _initEKYCSoapProxy();
  }
  
  public EKYCSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initEKYCSoapProxy();
  }
  
  private void _initEKYCSoapProxy() {
    try {
      eKYCSoap = (new org.tempuri.EKYCLocator()).getEKYCSoap();
      if (eKYCSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)eKYCSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)eKYCSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (eKYCSoap != null)
      ((javax.xml.rpc.Stub)eKYCSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.EKYCSoap getEKYCSoap() {
    if (eKYCSoap == null)
      _initEKYCSoapProxy();
    return eKYCSoap;
  }
  
  
}