package com.fedex.systemForm.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fedex.systemForm.dto.CpmsSystemFormServiceDTO;
import com.fedex.systemForm.repo.CpmsSystemFormServiceRepo;

@Service
public class CpmsSystemFormServiceService {
	
	@Autowired
	private CpmsSystemFormServiceRepo cpmsSystemFormServiceRepo;
	
	/* Sample JPA method to find all the shipments */
	public List<CpmsSystemFormServiceDTO> findAllShipments() {
		return cpmsSystemFormServiceRepo.findAll();
	}
	
	/* Sample JPA method to find specific shipment by number */
	public Optional<CpmsSystemFormServiceDTO> findByShipmentNumber(Long number) {
		return cpmsSystemFormServiceRepo.findById(number);
	}
	
	/* Sample JPA method to create new shipments */
	public CpmsSystemFormServiceDTO saveNewShipment(CpmsSystemFormServiceDTO shipment) {
		return cpmsSystemFormServiceRepo.save(shipment);
	}
	
	/* Sample JPA method to delete a shipment */
	public void deleteShipment(Long number) {
		cpmsSystemFormServiceRepo.deleteById(number);
	}

	/* Sample test method for Test Driven Development */
	public String findShipmentLocationByNumber(Long number) {
		return "Shipment " +number+" is located in Memphis.";
	}
}
