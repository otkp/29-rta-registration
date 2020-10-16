package org.epragati.cfms.vo;

import java.io.Serializable;

public class DataVO implements Serializable {
	private String paydata;

	public String getPaydata() {
		return paydata;
	}

	public void setPaydata(String paydata) {
		this.paydata = paydata;
	}

	private String deptCode;

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	
}
