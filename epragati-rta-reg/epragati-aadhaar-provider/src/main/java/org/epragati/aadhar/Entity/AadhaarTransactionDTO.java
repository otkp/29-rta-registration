package org.epragati.aadhar.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "AADHAAR_LOGS")
public class AadhaarTransactionDTO {
	
	@Id
	private String id;
	
	private AadhaarRequestLogEntity aadhaarRequest;
	
	private String aadhaarResponse;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the aadhaarRequest
	 */
	public AadhaarRequestLogEntity getAadhaarRequest() {
		return aadhaarRequest;
	}

	/**
	 * @param aadhaarRequest the aadhaarRequest to set
	 */
	public void setAadhaarRequest(AadhaarRequestLogEntity aadhaarRequest) {
		this.aadhaarRequest = aadhaarRequest;
	}

	/**
	 * @return the aadhaarResponse
	 */
	public String getAadhaarResponse() {
		return aadhaarResponse;
	}

	/**
	 * @param aadhaarResponse the aadhaarResponse to set
	 */
	public void setAadhaarResponse(String aadhaarResponse) {
		this.aadhaarResponse = aadhaarResponse;
	}
	
	
	

}
