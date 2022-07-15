package com.api.myfirstapi.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.myfirstapi.dtos.ParkingSpotDTO;
import com.api.myfirstapi.models.ParkingSpotModel;
import com.api.myfirstapi.services.ParkingSpotService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO) {
        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }

        ParkingSpotModel parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots() {
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id) {
    	Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    	
    	if (parkingSpotModelOptional.isPresent()) {    		
    		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    	}
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(
    		@PathVariable(value = "id") UUID id,
    		@RequestBody @Valid ParkingSpotDTO parkingSpotDTO
	) {
    	Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    	
    	if (parkingSpotModelOptional.isEmpty()) {    	
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    	}
    	
//    	Parse manual
//    	ParkingSpotModel parkingSpotModel = parkingSpotModelOptional.get();
//    	parkingSpotModel.setApartment(parkingSpotDTO.getApartment());
//    	parkingSpotModel.setBlock(parkingSpotDTO.getBlock());
//    	parkingSpotModel.setBrandCar(parkingSpotDTO.getBrandCar());
//    	parkingSpotModel.setColorCar(parkingSpotDTO.getColorCar());
//    	parkingSpotModel.setLicensePlateCar(parkingSpotDTO.getLicensePlateCar());
//    	parkingSpotModel.setModelCar(parkingSpotDTO.getModelCar());
//    	parkingSpotModel.setParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber());
//    	parkingSpotModel.setResponsibleName(parkingSpotDTO.getResponsibleName());

//		Parse Auto
    	ParkingSpotModel parkingSpotModel = new ParkingSpotModel();

    	BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
    	parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
    	parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
    	
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id) {
    	Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
    	
    	if (parkingSpotModelOptional.isPresent()) {
    		parkingSpotService.delete(parkingSpotModelOptional.get());
    		return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully!");
    	}
    	
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }
}
