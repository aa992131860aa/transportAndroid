package com.otqc.transbox.json;

import java.io.Serializable;

public	class OpoInfoContact implements Serializable {
	private String contactName;
	private String contactPhone;

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

}
