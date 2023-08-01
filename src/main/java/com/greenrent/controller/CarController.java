package com.greenrent.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenrent.dto.CarDTO;
import com.greenrent.dto.response.GRResponse;
import com.greenrent.dto.response.ResponseMessage;
import com.greenrent.service.CarService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/car")
@AllArgsConstructor
public class CarController {

	private CarService carService;
	
	
	//localhost:8080/car/admin/4b750117-9c21-4b1e-b1b4-1c3973280d62/add
	@PostMapping("/admin/{imageId}/add")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> saveCar(@PathVariable String imageId , @Valid @RequestBody CarDTO carDTO){
		
		carService.saveCar(carDTO, imageId);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.CAR_SAVED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	
	@GetMapping("/visitors/all")
	public ResponseEntity<List<CarDTO>> getAllCars(){
		List<CarDTO> carDTOs=carService.getAllCars();
		return ResponseEntity.ok(carDTOs);
	}
	
	//localhost:8080/car/visitors/2
	@GetMapping("/visitors/{id}")
	public ResponseEntity<CarDTO> getCarById(@PathVariable Long id){
		CarDTO carDTO=carService.findById(id);
		return ResponseEntity.ok(carDTO);
	}
	
	//aracları sayfa sayfa getiren yapı amacı cok falza araba var sa sayfa sayfa cekmeye yarıyor
	//8 baslangıcı
	//burada reposıtoryden metot yazıcak query işlemi için 
	
	//localhost:8080/car/visitors/pages?size=1&page=0&sort=id&direction=ASC
	@GetMapping("/visitors/pages")
	public ResponseEntity<Page<CarDTO>> getAllWithPage(@RequestParam("page") int page ,
			@RequestParam("size" )int size,
			@RequestParam("sort" )String prop,
			@RequestParam("direction" )Direction direction){
		
		Pageable pageable=PageRequest.of(page, size , Sort.by(direction , prop));
		Page<CarDTO> carPage =carService.findAllWithPage(pageable);
		
		return ResponseEntity.ok(carPage);
	}
	
	@PutMapping("/admin/auth")
	@PreAuthorize("hasRole ('ADMIN')")
	public ResponseEntity<GRResponse> updateCar(@RequestParam("id") Long id ,
												@RequestParam("imageId") String imageId,
												@Valid @RequestBody CarDTO carDTO){
		
		carService.updateCar(id, imageId, carDTO);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.CAR_UPDATED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		
		return ResponseEntity.ok(response);
		
	}
	
	@DeleteMapping("/admin/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> deleteCar(@PathVariable Long id){
		carService.removeById(id);
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.CAR_DELETEDD_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		
		return ResponseEntity.ok(response);
		
	}
	
}
