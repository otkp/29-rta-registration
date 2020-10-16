package org.epragati.cfms.vo;

public class CFMSOtherChallan {

	String accountNo;
	String amount;
	
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "CFMSOtherChallan [accountNo=" + accountNo + ", amount=" + amount + "]";
	}
	
	

}
