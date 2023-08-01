package com.greenrent.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.greenrent.dto.ReservationDTO;
import com.greenrent.dto.request.ReservationRequest;
import com.greenrent.dto.request.ReservationUpdateRequest;
import com.greenrent.dto.response.CarAvailabilityResponse;
import com.greenrent.dto.response.GRResponse;
import com.greenrent.dto.response.ResponseMessage;
import com.greenrent.service.CarService;
import com.greenrent.service.ReservationService;

import lombok.AllArgsConstructor;
import lombok.Delegate;

@AllArgsConstructor
@RestController
@RequestMapping("/reservations")
public class ReservationController {

	private ReservationService reservationService;
	private CarService carService;
	
	@PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<GRResponse> makeReservation(HttpServletRequest request , @RequestParam(value = "carId") Long carID,
														@Valid @RequestBody ReservationRequest reservationRequest){
		
		Long userId=(Long) request.getAttribute("id");
		reservationService.createReservation(reservationRequest, userId, carID);
		
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_SAVED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	
	//admın butun reservasyon bılgılerı dondurmek
	@GetMapping("/admin/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ReservationDTO>> getAllReservations(){
		List<ReservationDTO> reservations=reservationService.getAllReservations();
		return ResponseEntity.ok(reservations);
	}
	
	//admın bır reservasyon ıd ıle reservasyon bilgisini döndürmek için kullanılır
	@GetMapping("/{id}/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id){
		ReservationDTO reservationDTO=reservationService.findById(id);
		return ResponseEntity.ok(reservationDTO);
	}
	
	@PutMapping("/admin/auth")
	@PreAuthorize("hasRole('ADMIN') ")
	public ResponseEntity<GRResponse> updateReservation(HttpServletRequest request , @RequestParam(value = "carId") Long carID,
														@RequestParam(value = "reservationId") Long reservationId	,
														@Valid @RequestBody ReservationUpdateRequest reservationUpdateRequest){
		
		
		reservationService.updateReservation(  reservationId, carID , reservationUpdateRequest);
		
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_UPDATED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
		
	
	@PostMapping("/add/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> addReservation(@RequestParam(value = "userId") Long userId, 
													@RequestParam(value = "carId") Long carID,
														@Valid @RequestBody ReservationRequest reservationRequest){
		
		
		reservationService.createReservation(reservationRequest, userId, carID);
		
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_SAVED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	
	
	//adminin belli idli kullanıcını rezervasyonlarını goruntulemek ıcın
	@GetMapping("/admin/auth/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ReservationDTO>> getAllReservations(@RequestParam("userId") Long userId){
		List<ReservationDTO> reservations =reservationService.findAllByUserId(userId);
		return ResponseEntity.ok(reservations);
	}
	
	
	@GetMapping("/auth")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<GRResponse> chechCarIsAvaible (@RequestParam("carId") Long carId ,
	@RequestParam(value = "pickUpDateTime") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime pickUpTime,
	@RequestParam(value = "dropOffDateTime") @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss") LocalDateTime dropOfftime){
		
		boolean isNotAvailable =reservationService.chechCarAvailability(carId, pickUpTime, dropOfftime);
		
		
		
		Double totalPrice =reservationService.getTotalPrice(carId, pickUpTime, dropOfftime);
		
		CarAvailabilityResponse response = new CarAvailabilityResponse(!isNotAvailable , totalPrice , ResponseMessage.CAR_AVAİLABLE_MESSAGE , true);
		
		
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
		


	@DeleteMapping("/admin/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> deleteReservation(@PathVariable Long id){
		
		reservationService.removeById(id);
		
		GRResponse response = new GRResponse();
		response.setMessage(ResponseMessage.RESERVATION_DELETED_RESPONSE_MESSAGE);
		response.setSuccess(true);
		
		return new ResponseEntity<>(response , HttpStatus.OK);
		
	}
	
	//kısı kendısıne aıt olan rezervasyonu goruntulemek ıcın
	@GetMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<ReservationDTO> getUserReservationById(HttpServletRequest request , @PathVariable Long id){
		Long userId =(Long)request.getAttribute("id");
		
		ReservationDTO reservationDTO= reservationService.findByIdAndUserId(id, userId);
		return ResponseEntity.ok(reservationDTO);
		
	}
	
	@GetMapping("/auth/all")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<List<ReservationDTO>> getUserReservationsByUserId(HttpServletRequest request ){
		
		Long userId =(Long)request.getAttribute("id");
		//authentıca olmus kısıden bılgılerı buradan cekıyoruz
		
		
		List<ReservationDTO> reservations= reservationService.findAllByUserId(userId);
		return ResponseEntity.ok(reservations);
		
	}
	
	
}
