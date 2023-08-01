package com.greenrent.dto.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {

	private String newPassword;
	private String oldPassword;
}
