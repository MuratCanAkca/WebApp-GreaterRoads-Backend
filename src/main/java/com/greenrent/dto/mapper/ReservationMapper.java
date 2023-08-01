package com.greenrent.dto.mapper;

import org.mapstruct.Mapper;

import com.greenrent.domain.Reservation;
import com.greenrent.dto.request.ReservationRequest;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

	Reservation reservationRequestToReservation(ReservationRequest reservationRequest );
	
	
	
}
