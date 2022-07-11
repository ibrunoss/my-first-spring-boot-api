package com.api.myfirstapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.myfirstapi.repositories.ParkingSpotRepository;

@Service
public class ParkingSpotService {
    // @Autowired
    // ParkingSpotRepository parkingSpotRepository;


    final ParkingSpotRepository parkingSpotRepository;

    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }
}
