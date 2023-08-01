package com.greenrent.service;

import java.time.LocalDateTime;
import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.greenrent.domain.Car;
import com.greenrent.domain.Reservation;
import com.greenrent.domain.User;
import com.greenrent.domain.enums.ReservationStatus;
import com.greenrent.dto.ReservationDTO;
import com.greenrent.dto.mapper.ReservationMapper;
import com.greenrent.dto.request.ReservationRequest;
import com.greenrent.dto.request.ReservationUpdateRequest;
import com.greenrent.exception.BadRequestException;
import com.greenrent.exception.ResourceNotFoundException;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.repository.CarRepository;
import com.greenrent.repository.ReservationRepository;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ReservationService {

	private ReservationRepository reservationRepository;
	private CarRepository carRepository;
	//private CarService carService;
	private UserRepository userRepository;
	private ReservationMapper reservationMapper;
	
	public void createReservation(ReservationRequest reservationRequest , Long userId , Long carId) {
		
		checkReservationTimeIsCorrect(reservationRequest.getPickUpTime(), reservationRequest.getDropOffTime());
		
		Car car =carRepository.findById(carId).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,carId)));
		
		boolean carStatus=chechCarAvailability(carId, reservationRequest.getPickUpTime() ,reservationRequest.getDropOffTime());
		
		User user =userRepository.findById(userId).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,/*userId*/ "HATA FARKEDİLDİ!!!!")));
		
		Reservation reservation = reservationMapper.reservationRequestToReservation(reservationRequest);
		
		if(!carStatus) {
			reservation.setStatus(ReservationStatus.CREATED);
		}
		else {
			throw new BadRequestException(ErrorMEssage.CAR_NOT_AVAIBLE);
		}
	
		reservation.setCarId(car);
		reservation.setUserId(user);
		
		Double totalPrice =getTotalPrice(carId, reservation.getPickUpTime(), reservation.getDropOffTime());
		
		reservation.setTotalPrice(totalPrice);
		
		reservationRepository.save(reservation);
		
		
	}
	
	//total para ıcın hesaplama metotdu
	public Double getTotalPrice( Long carId ,LocalDateTime pickUpTime , LocalDateTime dropOffTime) {
	
		Car car =carRepository.findById(carId).orElseThrow(() -> new 
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,carId)));
		
		Long hours = (new Reservation()).getTotalHours(pickUpTime, dropOffTime);
		return car.getPricePerHour()*hours;
	}
	
	
	
	//arac musaitmi kontrol
	public boolean chechCarAvailability(Long carId , LocalDateTime pickUpTime , LocalDateTime dropOffTime) {
		
		ReservationStatus [] status = {ReservationStatus.CANCELLED,ReservationStatus.DONE};
		
		List<Reservation> existReservations = reservationRepository.checkCarStatus(carId , pickUpTime , dropOffTime , status);
		
		return !existReservations.isEmpty();
	}
	
	
	//rezervasyon saatini denetleyen bır yapı şimdiden öncesine rezervasyon alamazsın
	private void checkReservationTimeIsCorrect(LocalDateTime pickUpTime , LocalDateTime dropOffTime) {
		LocalDateTime now = LocalDateTime.now();
		
		if(pickUpTime.isBefore(now)) {
			throw new BadRequestException("HATA BURADAA");
		}
		
		boolean isEqual = pickUpTime.isEqual(dropOffTime)?true:false;
		boolean isBefore=pickUpTime.isBefore(dropOffTime)?true:false;
		
		if(isEqual ||! isBefore) {
			throw new BadRequestException(ErrorMEssage.RESERVATION_TIME_INCORRECT_MESSAGE);
		}
		
	}
	
	///*****************************************************
	//yukarısı komple rezervasyon olması için
	
	@Transactional(readOnly = true)
	public List<ReservationDTO> getAllReservations(){
		return reservationRepository.findAllBy();
	}
	
	
	@Transactional(readOnly = true)
	public ReservationDTO findById(Long id) {
		return reservationRepository.findDTOById(id).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, id)));
	}
	
	public void updateReservation(Long reservationId , Long carId , ReservationUpdateRequest reservationUpdateRequest) {
		
		Reservation reservation =reservationRepository.findById(reservationId).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, reservationId)));
		
		checkReservationTimeIsCorrect(reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());
		
		Car car =carRepository.findById(carId).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,carId)));
		
		boolean carStatus=chechCarAvailability(carId, reservationUpdateRequest.getPickUpTime() ,reservationUpdateRequest.getDropOffTime());
		
		if(reservationUpdateRequest.getPickUpTime().compareTo(reservation.getPickUpTime())==0 && 
		   reservationUpdateRequest.getDropOffTime().compareTo(reservation.getDropOffTime())==0 &&
		   car.getId().equals(reservation.getCarId().getId())) {
			
			reservation.setStatus(reservationUpdateRequest.getStatus());
			
		}
		else if(carStatus) {
			throw new BadRequestException(ErrorMEssage.CAR_NOT_AVAIBLE);
		}
		
		Double totalPrice =getTotalPrice(carId,reservationUpdateRequest.getPickUpTime(), reservationUpdateRequest.getDropOffTime());
		
		reservation.setTotalPrice(totalPrice);
		reservation.setCarId(car);
		reservation.setPickUpTime(reservationUpdateRequest.getPickUpTime());
		reservation.setDropOffTime(reservationUpdateRequest.getDropOffTime());
		reservation.setPickUpLocation(reservationUpdateRequest.getPickUpLocation());
		reservation.setDropOffLocation(reservation.getDropOffLocation());
		
		reservationRepository.save(reservation);
		
		
	}
	
	@Transactional(readOnly = true)
	public List<ReservationDTO> findAllByUserId(Long userId){
		
		User user = userRepository.findById(userId).orElseThrow(()-> new 
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, userId)));
		
		return reservationRepository.findAllByUserId(user);
	}
	
	
	public void removeById(Long id) {
		
		boolean exist =reservationRepository.existsById(id);
		
		if(!exist) {
			throw new ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, id));
		}
		
		reservationRepository.deleteById(id);
	}
	@Transactional(readOnly = true)
	public ReservationDTO findByIdAndUserId(Long id , Long userId) {
		User user =userRepository.findById(userId).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, userId)));
	
		return reservationRepository.findByIdAndUserId(id, user).orElseThrow(()-> new 
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		
	
	}
	/*
	public List<ReservationDTO> findAllByUser(Long userId){
		User user =userRepository.findById(userId).orElseThrow(()-> new 
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, userId)));
		
		return reservationRepository.findAllByUserId(user);
		
	}*///bu yukarıda aynısı yazılmıs zatem
	
}
