package com.greenrent.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.greenrent.domain.Car;
import com.greenrent.domain.Reservation;
import com.greenrent.domain.User;
import com.greenrent.helper.ExcelReportHelper;
import com.greenrent.repository.CarRepository;
import com.greenrent.repository.ReservationRepository;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {

	UserRepository userRepository;
	CarRepository carRepository;
	ReservationRepository reservationRepository;
	
	
	public ByteArrayInputStream getUSerReport() throws IOException {
		List<User> users=userRepository.findAll();
		return ExcelReportHelper.getUsersExcelReport(users);
	}
	
	public ByteArrayInputStream getCarReport() throws IOException {
		List<Car> users=carRepository.findAll();
		return ExcelReportHelper.getCarsExcelReport(users);
	}
	
	public ByteArrayInputStream getReservationReport() throws IOException {
		List<Reservation> reservation=reservationRepository.findAll();
		return ExcelReportHelper.getReservationsExcelReport(reservation);
	}
	
	
}
