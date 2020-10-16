package org.epragati.enity.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "M_NOTIFICATION_TEMPLATE")
public class NotificationTemplateEntity {

	@Id
	@Column(name = "TEMPLATE_ID")	
	private Integer templateId;
	
	@Column(name = "TEMPLATE_NAME", length = 20)
	private String templateNage;
	
	@Column(name = "TEMPLATE_DESC", length = 140)
	private String templateDesc;
	
	@Column(name = "ISACTIVE")
	private Boolean isActive;
	
	@Column(name = "SERVICE_ID")
	private  Integer serviceId;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getTemplateNage() {
		return templateNage;
	}

	public void setTemplateNage(String templateNage) {
		this.templateNage = templateNage;
	}

	public String getTemplateDesc() {
		return templateDesc;
	}

	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}	
	
}
