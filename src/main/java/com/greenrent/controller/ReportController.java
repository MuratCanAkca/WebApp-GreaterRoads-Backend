package com.greenrent.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greenrent.exception.BadRequestException;
import com.greenrent.exception.ExcelReportException;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.service.ReportService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/excel")
@AllArgsConstructor
public class ReportController {

	ReportService reportService;
	
	@GetMapping("/download/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Resource> getUserReport(){
		
		String fileName="users.xlsx";
		
		try {
			ByteArrayInputStream bais=reportService.getUSerReport();
			InputStreamResource file = new InputStreamResource(bais);
			
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION , "attachment;filename="+fileName).
					contentType(MediaType.parseMediaType("application/vmd.ms-excel")).body(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ExcelReportException(ErrorMEssage.EXCEL_REPORT_CREATION_MESSAGE);
		}
		
	}
	
	@GetMapping("/download/cars")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Resource> getCarReport(){
		
		String fileName="cars.xlsx";
		
		try {
			ByteArrayInputStream bais=reportService.getCarReport();
			InputStreamResource file = new InputStreamResource(bais);
			
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION , "attachment;filename="+fileName).
					contentType(MediaType.parseMediaType("application/vmd.ms-excel")).body(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ExcelReportException(ErrorMEssage.EXCEL_REPORT_CREATION_MESSAGE);
		}
		
	}
	
	@GetMapping("/download/reservations")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Resource> getReservationReport(){
		
		String fileName="reservations.xlsx";
		
		try {
			ByteArrayInputStream bais=reportService.getReservationReport();
			InputStreamResource file = new InputStreamResource(bais);
			
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION , "attachment;filename="+fileName).
					contentType(MediaType.parseMediaType("application/vmd.ms-excel")).body(file);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ExcelReportException(ErrorMEssage.EXCEL_REPORT_CREATION_MESSAGE);
		}
		
	}
	
	
	
}
