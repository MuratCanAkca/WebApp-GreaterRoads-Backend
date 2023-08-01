package com.greenrent.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenrent.dto.UserDTO;
import com.greenrent.dto.request.AdminUserUpdateRequest;
import com.greenrent.dto.request.UpdatePasswordRequest;
import com.greenrent.dto.request.UserUpdateRequest;
import com.greenrent.dto.response.GRResponse;
import com.greenrent.dto.response.ResponseMessage;
import com.greenrent.service.UserService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
	
	private UserService userService;

	@GetMapping("/auth/all")
	// bu endpoınt admın tarafından yapıldıgını
	@PreAuthorize("hasRole('ADMIN') ")
	public ResponseEntity<List<UserDTO>> getAllUsers(){
		List<UserDTO> users =userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
	
	//sistemdekı herhangı bır kullanıcı kendı bılgılerını getırıyor
	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<UserDTO> getUserById(HttpServletRequest request){
		Long id =(Long)request.getAttribute("id");
		UserDTO userDTO = userService.findById(id);
		return ResponseEntity.ok(userDTO);
	}
	
	///******************************
	//5 baslangıc 
	
	
	
	//gene pageleme işemi yaptık gerek var mı bence yoktu
	//localhost:8080/user/auth/pages?size=5&page=0&sort=id&direction=ASC
	@GetMapping("/auth/pages")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<UserDTO>> getAllUsersByPage(@RequestParam("page") int page ,
			@RequestParam("size" )int size,
			@RequestParam("sort" )String prop,
			@RequestParam("direction" )Direction direction){
		
		Pageable pageable=PageRequest.of(page, size , Sort.by(direction , prop));
		
		
		Page<UserDTO> userDTOPage =userService.getUserPage(pageable);
		
		return ResponseEntity.ok(userDTOPage);
		
	}
	
	//Admın herhangı ıd ıle kısıyı getırmesı
	//localhost:8080/user/9/auth
	@GetMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> getUserByIdAdmin(@PathVariable Long id){
		UserDTO user = userService.findById(id);
		return ResponseEntity.ok(user);
	}
	

	//localhost:8080/user/auth
	@PatchMapping("/auth")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<GRResponse> updatePassword(HttpServletRequest httpServletRequest , @RequestBody UpdatePasswordRequest passwordRequest){
		
		Long id=(Long)httpServletRequest.getAttribute("id");
		userService.updatePasswprd(id, passwordRequest);
		
		GRResponse response= new GRResponse();
		response.setMessage(ResponseMessage.PASSWORD_CHANGED_MESSAGE);
		response.setSuccess(true);
		
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<GRResponse> updateUser(HttpServletRequest httpServletRequest , @Valid  @RequestBody UserUpdateRequest userUpdateRequest){
		
		Long id =(Long) httpServletRequest.getAttribute("id");
		userService.updateUser(id , userUpdateRequest);
		
		GRResponse response= new GRResponse();
		response.setMessage(ResponseMessage.UPDATE_RESPONSE_MESSAGE);
		response.setSuccess(true);
	
		return ResponseEntity.ok(response);
		
	}
	
	//localhost:8080/user/9/auth
	@DeleteMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> deleteUser(@PathVariable Long id){
		userService.removeById(id);
		
		GRResponse response= new GRResponse();
		response.setMessage(ResponseMessage.DELETE_RESPONSE_MESSAGE);
		response.setSuccess(true);
	
		return ResponseEntity.ok(response);
		
		
	}
	
	//localhost:8080/user/9/auth
	//admının kullanıcı upradet etmesı sıfre dahıl
	@PutMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GRResponse> updateUserAuth(@PathVariable Long id , @Valid  @RequestBody AdminUserUpdateRequest adminUserUpdateRequest){
		

		userService.updateUserAuth(id , adminUserUpdateRequest);
		
		GRResponse response= new GRResponse();
		response.setMessage(ResponseMessage.UPDATE_RESPONSE_MESSAGE);
		response.setSuccess(true);
	
		return ResponseEntity.ok(response);
		
	}
	 
	
}
