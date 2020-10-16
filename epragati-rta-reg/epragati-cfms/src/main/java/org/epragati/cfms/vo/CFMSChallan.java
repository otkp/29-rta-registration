package org.epragati.cfms.vo;

public class CFMSChallan {

	String headOfAccount;
	String dDoCode;
	String serviceCode;
	String deptHOAAmount;
	
	
	public String getHeadOfAccount() {
		return headOfAccount;
	}
	public void setHeadOfAccount(String headOfAccount) {
		this.headOfAccount = headOfAccount;
	}
	public String getdDoCode() {
		return dDoCode;
	}
	public void setdDoCode(String dDoCode) {
		this.dDoCode = dDoCode;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getDeptHOAAmount() {
		return deptHOAAmount;
	}
	public void setDeptHOAAmount(String deptHOAAmount) {
		this.deptHOAAmount = deptHOAAmount;
	}
	@Override
	public String toString() {
		return "CFMSChallan [headOfAccount=" + headOfAccount + ", dDoCode=" + dDoCode + ", serviceCode=" + serviceCode
				+ ", deptHOAAmount=" + deptHOAAmount + "]";
	}
	
	
	

}
