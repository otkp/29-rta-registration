package org.epragati.cfms.vo;

import java.util.List;

public class CFMSPaymentReqParams {

	String deptCode;
	String deptTransactionId;
	String remitterName;
	String deptRId;
	String totalAmount;
	String redirectUrl;
	String userId;
	String pwd;
	List<CFMSChallan> cFMSChallanList;
	List<CFMSOtherChallan> cFMSOtherChallanList;	
	
	String cfmsRequestUrl;
	
	
	
	public String getCfmsRequestUrl() {
		return cfmsRequestUrl;
	}
	public void setCfmsRequestUrl(String cfmsRequestUrl) {
		this.cfmsRequestUrl = cfmsRequestUrl;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getDeptTransactionId() {
		return deptTransactionId;
	}
	public void setDeptTransactionId(String deptTransactionId) {
		this.deptTransactionId = deptTransactionId;
	}
	public String getRemitterName() {
		return remitterName;
	}
	public void setRemitterName(String remitterName) {
		this.remitterName = remitterName;
	}
	public String getDeptRId() {
		return deptRId;
	}
	public void setDeptRId(String deptRId) {
		this.deptRId = deptRId;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public List<CFMSChallan> getcFMSChallanList() {
		return cFMSChallanList;
	}
	public void setcFMSChallanList(List<CFMSChallan> cFMSChallanList) {
		this.cFMSChallanList = cFMSChallanList;
	}
	public List<CFMSOtherChallan> getcFMSOtherChallanList() {
		return cFMSOtherChallanList;
	}
	public void setcFMSOtherChallanList(List<CFMSOtherChallan> cFMSOtherChallanList) {
		this.cFMSOtherChallanList = cFMSOtherChallanList;
	}
	
	
	@Override
	public String toString() {
		return "CFMSPaymentReqParams [deptCode=" + deptCode + ", deptTransactionId=" + deptTransactionId
				+ ", remitterName=" + remitterName + ", deptRId=" + deptRId + ", totalAmount=" + totalAmount
				+ ", redirectUrl=" + redirectUrl + ", cFMSChallanList=" + cFMSChallanList + ", cFMSOtherChallanList="
				+ cFMSOtherChallanList + "]";
	}
	
}
