package com.greenrent.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.greenrent.exception.message.ApiResponseError;

@ControllerAdvice
public class GreenRentExceptionHandler extends ResponseEntityExceptionHandler{
	
	private ResponseEntity<Object> buildResponseEntity(ApiResponseError error){
		return new ResponseEntity<>(error , error.getStatus());
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex , WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.NOT_FOUND,"GreenRent "+ ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(ConflictException.class)
	protected ResponseEntity<Object> handleConflictException(ConflictException ex , WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.CONFLICT, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex , WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}


	//bu validasyon hatasıdır eger bunu yazmasak uzunca bır error cıkardı ama sımdı basıt bır hata cıkıyor bunun ıcın 
	//postmande ayrı bır request actım
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<String> errors =ex.getBindingResult().getFieldErrors().
		stream().map(e-> e.getDefaultMessage()).
		collect(Collectors.toList());
		
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, errors.get(0).toString() , request.getDescription(false));
		return buildResponseEntity(error);
		
		
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	//eger belırlenen exceptıonlar dısında bır exceptıon gelırsa asagıda kı calısacak
	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<Object> handleGeneralException(RuntimeException ex , WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(ImageFileException.class)
	protected ResponseEntity<Object> handleImageFileException(ImageFileException ex , WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(ExcelReportException.class)
	protected ResponseEntity<Object> handleExcelReportException(ExcelReportException ex , WebRequest request){
		ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
		return buildResponseEntity(error);
	}
}
