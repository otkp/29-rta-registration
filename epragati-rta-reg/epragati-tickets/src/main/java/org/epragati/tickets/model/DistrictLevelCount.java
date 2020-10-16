package org.epragati.tickets.model;

public class DistrictLevelCount {

	private String districtName;
	private Long count;
	private Long dlcount;
	private Long regCount;

	public Long getDlcount() {
		return dlcount;
	}

	public void setDlcount(Long dlcount) {
		this.dlcount = dlcount;
	}

	public Long getRegCount() {
		return regCount;
	}

	public void setRegCount(Long regCount) {
		this.regCount = regCount;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
