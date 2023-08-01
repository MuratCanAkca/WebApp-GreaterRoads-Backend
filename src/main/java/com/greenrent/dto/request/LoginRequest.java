package com.greenrent.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	@Email(message = "Please provide a correct email")
	private String email;
	@NotNull(message = "Please provide a passord")
	private String password;
	
}
