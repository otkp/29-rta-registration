
package nic.transport.homologation.web.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getVehicleInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getVehicleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transactionPass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regnNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="chasiNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getVehicleInfo", propOrder = {
    "userId",
    "transactionPass",
    "regnNo",
    "chasiNo"
})
public class GetVehicleInfo {

    protected String userId;
    protected String transactionPass;
    protected String regnNo;
    protected String chasiNo;

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the transactionPass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionPass() {
        return transactionPass;
    }

    /**
     * Sets the value of the transactionPass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionPass(String value) {
        this.transactionPass = value;
    }

    /**
     * Gets the value of the regnNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * Sets the value of the regnNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegnNo(String value) {
        this.regnNo = value;
    }

    /**
     * Gets the value of the chasiNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChasiNo() {
        return chasiNo;
    }

    /**
     * Sets the value of the chasiNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChasiNo(String value) {
        this.chasiNo = value;
    }

}
