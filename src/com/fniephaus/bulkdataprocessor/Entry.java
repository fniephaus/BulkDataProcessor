package com.fniephaus.bulkdataprocessor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fniephaus.bulkdataprocessor.helpers.Configuration.StatusType;

@Entity
@Table(name = "entry")
public class Entry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "field")
	private String field;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status")
	private StatusType status;

	public Entry() {
	}

	public int getID() {
		return id;
	}

	public String getField() {
		return field;
	}

	public void setStatus(StatusType pStatus) {
		status = pStatus;
	}
}