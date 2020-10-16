package org.epragati.enity.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "M_NOTIFICATION_MSG_PARAMS")
public class NotificationMessageParams {
	
	@Id
	@Column(name = "ID")	
	private Integer id;
	
	@Column(name = "PARAM_KEY")	
	private String paramKey;
	
	@Column(name = "TEMPLATE_ID")
	private  Integer templateId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	

}
