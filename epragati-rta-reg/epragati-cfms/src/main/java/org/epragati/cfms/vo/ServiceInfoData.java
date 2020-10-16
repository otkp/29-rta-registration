package org.epragati.cfms.vo;

public class ServiceInfoData {

		String serviceCode;
		String serviceData;
		
		public String getServiceCode() {
			return serviceCode;
		}
		public void setServiceCode(String serviceCode) {
			this.serviceCode = serviceCode;
		}
		public String getServiceData() {
			return serviceData;
		}
		public void setServiceData(String serviceData) {
			this.serviceData = serviceData;
		}
		@Override
		public String toString() {
			return "ServiceInfoData [serviceCode=" + serviceCode + ", serviceData=" + serviceData + "]";
		}
		
		
		
}
