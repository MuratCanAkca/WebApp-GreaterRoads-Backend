package com.greenrent.exception.message;

public class ErrorMEssage {

	public final static String RESOURCE_NOT_FOUND_MESSAGE="Resource with id %d not found";
	//strıng format metodunda daha cok kullanılacak
	
	public final static String USER_NOT_FOUND_MESSAGE="Resource with email %s not found";
	
	public final static String EMAIL_ALREADY_EXIST="Email already exist:%s";
	
	public final static String ROLE_NOT_FOUND_MESSAGE="Role with name %s not found";
	
	public final static String NOT_PERMITTED_METHOT_MESSAGE="You dont have any permission to change this value";
	
	public final static String PASSWORD_NOT_MATCHED="Your password are not matched";
	
	public final static String IMAGE_NOT_FOUND_MESSAGE="ImageFile with id %s not found";
	
	public final static String RESERVATION_TIME_INCORRECT_MESSAGE="Reservation pick up time or drop off time not correctly";
	
	public final static String CAR_NOT_AVAIBLE="Car is not avaible for selected time";
	
	public final static String EXCEL_REPORT_CREATION_MESSAGE="Error occurred while generation excel report";
	
	public final static String CAR_USED_BY_RESERVATION_MESSAGE="Car couldn't be deleted. Car is used by a reservation";

	public final static String USER_USED_BY_RESERVATION_MESSAGE="User couldn't be deleted. Car is used by a reservation";
	
}
