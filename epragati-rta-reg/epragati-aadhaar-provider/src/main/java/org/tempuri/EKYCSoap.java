/**
 * EKYCSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface EKYCSoap extends java.rmi.Remote {
    public org.tempuri.AADHAAR_EKYC_FINGER_AUTHENTICATIONResponseAADHAAR_EKYC_FINGER_AUTHENTICATIONResult AADHAAR_EKYC_FINGER_AUTHENTICATION(java.lang.String uid, java.lang.String tid, java.lang.String udc, java.lang.String ip, java.lang.String srt, java.lang.String crt, java.lang.String skey, java.lang.String pid, java.lang.String hmac, java.lang.String ci, java.lang.String bt, java.lang.String pincode, java.lang.String version, java.lang.String scheme, java.lang.String department, java.lang.String service, java.lang.String consent, java.lang.String attemptcount, java.lang.String rdsId, java.lang.String rdsVer, java.lang.String dpId, java.lang.String dc, java.lang.String mi, java.lang.String mc) throws java.rmi.RemoteException;
}
