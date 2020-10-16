package org.epragati.enity.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.epragati.constants.NotificationTypeEnum;

@Entity
@Table(name = "M_NOTIFICATION_CONFIG")
public class NotificationConfigurationDetailsEntity {
	
	@Id
	@Column(name = "CONFIG_ID")	
	private Long configId;
	
	@Column(name = "TYPE")
	private NotificationTypeEnum type;
	
	@Column(name = "TEMPLATE_ID")
	private  Integer templateId;
	
	@Column(name = "TEMPLATE_MSG")
	private  String message;
	
	@Column(name = "SERVE_PROV_ID")
	private Long serviceProviderId;
	
	@Column(name = "ISACTIVE")
	private Boolean isActive;


	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public NotificationTypeEnum getType() {
		return type;
	}

	public void setType(NotificationTypeEnum type) {
		this.type = type;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
