/**
 * LicRegVCRDetailsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class LicRegVCRDetailsLocator extends org.apache.axis.client.Service implements org.tempuri.LicRegVCRDetails {

    private static final long serialVersionUID = 7059248803171269200L;

    public LicRegVCRDetailsLocator() {
    }


    public LicRegVCRDetailsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LicRegVCRDetailsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LicRegVCRDetailsSoap
    private java.lang.String LicRegVCRDetailsSoap_address = "http://202.65.142.140/poswebservice/LicRegVCRDetails.asmx";

    public java.lang.String getLicRegVCRDetailsSoapAddress() {
        return LicRegVCRDetailsSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LicRegVCRDetailsSoapWSDDServiceName = "LicRegVCRDetailsSoap";

    public java.lang.String getLicRegVCRDetailsSoapWSDDServiceName() {
        return LicRegVCRDetailsSoapWSDDServiceName;
    }

    public void setLicRegVCRDetailsSoapWSDDServiceName(java.lang.String name) {
        LicRegVCRDetailsSoapWSDDServiceName = name;
    }

    public org.tempuri.LicRegVCRDetailsSoap getLicRegVCRDetailsSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LicRegVCRDetailsSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLicRegVCRDetailsSoap(endpoint);
    }

    public org.tempuri.LicRegVCRDetailsSoap getLicRegVCRDetailsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.LicRegVCRDetailsSoapStub _stub = new org.tempuri.LicRegVCRDetailsSoapStub(portAddress, this);
            _stub.setPortName(getLicRegVCRDetailsSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLicRegVCRDetailsSoapEndpointAddress(java.lang.String address) {
        LicRegVCRDetailsSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.LicRegVCRDetailsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.LicRegVCRDetailsSoapStub _stub = new org.tempuri.LicRegVCRDetailsSoapStub(new java.net.URL(LicRegVCRDetailsSoap_address), this);
                _stub.setPortName(getLicRegVCRDetailsSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("LicRegVCRDetailsSoap".equals(inputPortName)) {
            return getLicRegVCRDetailsSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "LicRegVCRDetails");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "LicRegVCRDetailsSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LicRegVCRDetailsSoap".equals(portName)) {
            setLicRegVCRDetailsSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
