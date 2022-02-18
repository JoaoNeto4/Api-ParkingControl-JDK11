
package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.services.ParkingSpotService;
import com.api.parkingcontrol.models.ParkingSpotModel;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
//import javax.persistence.Entity;
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

@RestController
@CrossOrigin(origins="*", maxAge=3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    
    final ParkingSpotService parkingSpotService;
    
    public ParkingSpotController(ParkingSpotService parkingSpotService){
        this.parkingSpotService=parkingSpotService;
    }
    
    
    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }
        //ParkingSpotModel parkingSpotModel=new ParkingSpotModel();//( abaixo novidade do jdk11)
        var parkingSpotModel=new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
        /*utilizando o Postman metodo:
        -->Headers-->Key-->"Content-Type"
        -->Headers-->Value-->"application/json"
            POST: http://localhost:8080/parking-spot
                {
                    "parkingSpotNumber": "205b",
                    "licensePlateCar": "RRS8562",
                    "brandCar": "Audi",
                    "modelCar": "Q5",
                    "colorCar": "Black",
                    "responsibleName": "Antonio",
                    "apartment": "205",
                    "block": "B"
                }
        */
    }
    
    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
        /*utilizando o Postman metodo:
            GET: http://localhost:8080/parking-spot
        */
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value="id")UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional=parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not Found!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
        /*utilizando o Postman metodo:
            GET: http://localhost:8080/parking-spot/id
        */
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot (@PathVariable(value="id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional=parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not Found!");
        }
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot delete sucessfully.");
        /*
            DELETE: http://localhost:8080/parking-spot/id
        */
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot (@PathVariable(value="id") UUID id, 
            @RequestBody @Valid ParkingSpotDto parkigSportDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional=parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not Found!");
        }
        /* 
        var  parkingSpotModel=new  ParkingSpotModel();//realizar das duas maneiras
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel))
        */
        var  parkingSpotModel = parkingSpotModelOptional.get();
        parkingSpotModel.setParkingSpotNumber(parkigSportDto.getParkingSpotNumber());
        parkingSpotModel.setLicensePlateCar(parkigSportDto.getParkingSpotNumber());
        parkingSpotModel.setModelCar(parkigSportDto.getLicensePlateCar());
        parkingSpotModel.setBrandCar(parkigSportDto.getModelCar());
        parkingSpotModel.setColorCar(parkigSportDto.getColorCar());
        parkingSpotModel.setResponsibleName(parkigSportDto.getResponsibleName());
        parkingSpotModel.setApartment(parkigSportDto.getApartment());
        parkingSpotModel.setBlock(parkigSportDto.getBlock());
                
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
        /*
            PUT: http://localhost:8080/parking-spot/id
        */
    }
}
