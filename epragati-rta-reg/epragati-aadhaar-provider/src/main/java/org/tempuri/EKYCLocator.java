/**
 * EKYCLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class EKYCLocator extends org.apache.axis.client.Service implements org.tempuri.EKYC {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EKYCLocator() {
    }


    public EKYCLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EKYCLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EKYCSoap
    private java.lang.String EKYCSoap_address = "http://125.16.9.139:83/APONLINEKUASERVICEPROD2.1/APONLINEKYCSERVICE2.1.asmx";

    public java.lang.String getEKYCSoapAddress() {
        return EKYCSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EKYCSoapWSDDServiceName = "EKYCSoap";

    public java.lang.String getEKYCSoapWSDDServiceName() {
        return EKYCSoapWSDDServiceName;
    }

    public void setEKYCSoapWSDDServiceName(java.lang.String name) {
        EKYCSoapWSDDServiceName = name;
    }

    public org.tempuri.EKYCSoap getEKYCSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EKYCSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEKYCSoap(endpoint);
    }

    public org.tempuri.EKYCSoap getEKYCSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.EKYCSoapStub _stub = new org.tempuri.EKYCSoapStub(portAddress, this);
            _stub.setPortName(getEKYCSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEKYCSoapEndpointAddress(java.lang.String address) {
        EKYCSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.EKYCSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.EKYCSoapStub _stub = new org.tempuri.EKYCSoapStub(new java.net.URL(EKYCSoap_address), this);
                _stub.setPortName(getEKYCSoapWSDDServiceName());
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
        if ("EKYCSoap".equals(inputPortName)) {
            return getEKYCSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "EKYC");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "EKYCSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EKYCSoap".equals(portName)) {
            setEKYCSoapEndpointAddress(address);
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
