package com.fedex.systemForm.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CpmsSystemFormServiceDTO {
	
	@Id
	private String shipmentNumber;
	private String weight;
	private String destination;
	
	
	public String getShipmentNumber() {
		return shipmentNumber;
	}
	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
		
}
