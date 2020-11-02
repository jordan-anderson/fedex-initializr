package com.fedex.systemForm.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fedex.systemForm.dto.CpmsSystemFormServiceDTO;
import com.fedex.systemForm.services.CpmsSystemFormServiceService;

@RestController
public class CpmsSystemFormServiceController {
	
	private Logger logger = LoggerFactory.getILoggerFactory().getLogger("CpmsSystemFormServiceController");

	@Autowired
	private CpmsSystemFormServiceService cpmsSystemFormServiceService;
	
	/* EndPoint to get all shipments */
	/* @GetMapping shortcut for @RequestMapping(method = RequestMethod.GET) */
	
	@GetMapping(value = "/api/v1/shipments/")
	public ResponseEntity<Object> getAllShipments(){
		
		logger.info("Reteriving all Shipments");
		
		List<CpmsSystemFormServiceDTO> shipments = cpmsSystemFormServiceService.findAllShipments();
		
		if(shipments.isEmpty()) {					
			logger.info("Looks like there are no shipments");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(shipments, HttpStatus.OK);
	}
	
	/* EndPoint to get a shipment by Number */
	
	@RequestMapping(path = "/api/v1/shipmentNbr/{number}")
	public ResponseEntity<Object> findShipmentLocationByNumber(@PathVariable("number") Long number){
		
		logger.info("Reteriving shipment location by number" + number);
		
		String  shipmentsLocation = cpmsSystemFormServiceService.findShipmentLocationByNumber(number);
		if(shipmentsLocation.isEmpty()) {					
			logger.info("Looks like shipment location is not recorded");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(shipmentsLocation, HttpStatus.OK);		
	}
	

}
