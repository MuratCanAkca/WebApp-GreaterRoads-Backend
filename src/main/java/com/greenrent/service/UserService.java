package com.greenrent.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greenrent.domain.Role;
import com.greenrent.domain.User;
import com.greenrent.domain.enums.RoleType;
import com.greenrent.dto.UserDTO;
import com.greenrent.dto.mapper.UserMapper;
import com.greenrent.dto.request.AdminUserUpdateRequest;
import com.greenrent.dto.request.RegisterRequest;
import com.greenrent.dto.request.UpdatePasswordRequest;
import com.greenrent.dto.request.UserUpdateRequest;
import com.greenrent.exception.BadRequestException;
import com.greenrent.exception.ConflictException;
import com.greenrent.exception.ResourceNotFoundException;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.repository.ReservationRepository;
import com.greenrent.repository.RoleRepository;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
//@NoArgsConstructor
public class UserService {
	
	// gelen register requestı Usere donusturup kaydetmek ıcın
	
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private ReservationRepository reservationRepository;
	private PasswordEncoder passwordEncoder;
	
	//******************
	//1. Yöntem sonrası
	
	private UserMapper userMapper;

	//register işlemi yapılacak yer
	public void register (RegisterRequest registerRequest) {
		
		if(userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new ConflictException(String.format(ErrorMEssage.EMAIL_ALREADY_EXIST,registerRequest.getEmail()));
		} 
		
		String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
		
		//verilecek rolu verıtabanında dogrulamak
		Role role =roleRepository.findByName(RoleType.ROLE_CUSTOMER).
		orElseThrow(()->new ResourceNotFoundException(String.format( ErrorMEssage.ROLE_NOT_FOUND_MESSAGE,RoleType.ROLE_CUSTOMER.name())));
		//değiştirilen kısım                                                        burası***
		
		Set<Role> roles =  new HashSet<>();
		roles.add(role);
		
		User user = new User();
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(encodedPassword);
		user.setPhoneNumber(registerRequest.getPhoneNumber());
		user.setAddress(registerRequest.getAddress());
		user.setZipCode(registerRequest.getZipcode());
		user.setRoles(roles);
		
		userRepository.save(user);
		
	}
	
	//****************************************************************************
	//1. Yorum
	
	public List<UserDTO> getAllUsers(){
		List<User> users = userRepository.findAll();

		return userMapper.map(users);
	}
	
	//*************************
	//UserControllerda page dönen bir metot
	
	public Page<UserDTO> getUserPage(Pageable pageable){
		Page<User> users=userRepository.findAll(pageable);
		
		Page<UserDTO> dtoPage =users.map(new Function<User , UserDTO>(){
			@Override
			public UserDTO apply(User user) {
				return userMapper.userToUserDTO(user);
			}
		});
		
		return dtoPage;
	}
	
	
	
	//********************************,
	//2. yorum
	
	public UserDTO findById(Long id) {
		User user =userRepository.findById(id).orElseThrow(()-> 
		new ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		
		return userMapper.userToUserDTO(user);
		//mapstract yardımıyla  userı userDTOya 
		
	}
	
	//*************************
	//5 devam
	
	public void updatePasswprd(Long id , UpdatePasswordRequest passwordRequest) {
		
		Optional<User> userOpt =userRepository.findById(id)/*.orElseThrow()*/;
		User user =  userOpt.get();
		
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMEssage.NOT_PERMITTED_METHOT_MESSAGE);
		}
		/*
		//dısardan gelen sıfre dogru mu kontrolu
		if(!BCrypt.hashpw(passwordRequest.getOldPassword(), user.getPassword()).equals(user.getPassword())) {
			throw new BadRequestException(ErrorMEssage.PASSWORD_NOT_MATCHED);
		}
		 */
		//bu yapı daha ıyıymıs o yuzden yukardakını yoruma aldık bunu yaptık
		if(!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
			throw new BadRequestException(ErrorMEssage.PASSWORD_NOT_MATCHED);
		}
		
		
		
		
		String hashedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
		user.setPassword(hashedPassword);
		userRepository.save(user);
		
	}
	
	
	@Transactional
	public void updateUser(Long id , UserUpdateRequest userUpdateRequest) {
		//emaıl denetımı yapıcak yanı baska kayıtta olan emaıl olmamalı
		//existsByEmaıl emalı kontrolu yapar hemde unıqu sekılde
		boolean emailExist =userRepository.existsByEmail(userUpdateRequest.getEmail());
		
		
		// var olan bır user olmaması lazım kontrol edılecek
		User user = userRepository.findById(id).get();
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMEssage.NOT_PERMITTED_METHOT_MESSAGE);
		}
		
		if(emailExist && !userUpdateRequest.getEmail().equals(user.getEmail())) {
			throw new ConflictException(ErrorMEssage.EMAIL_ALREADY_EXIST);
		}
		
		userRepository.update(id , userUpdateRequest.getFirstName(),
				userUpdateRequest.getLastName(),
				userUpdateRequest.getPhoneNumber(),
				userUpdateRequest.getEmail(),
				userUpdateRequest.getAddress(),
				userUpdateRequest.getZipCode()
				);
		
	}
	
	public void removeById(Long id) {
		
		User user =userRepository.findById(id).orElseThrow(()-> new
				ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE,id)));
		
		//eger rezervasyonda user varsa silme işlemi yapılmaması ıcın sonradan eklenen kod burası
				boolean exists=reservationRepository.existsByUserId(user);
				
				if(exists) {
					throw new BadRequestException(ErrorMEssage.USER_USED_BY_RESERVATION_MESSAGE);
				}
				
				//buraya kadardı
		
		
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMEssage.NOT_PERMITTED_METHOT_MESSAGE);
		}
		
		userRepository.deleteById(id);
		
		
	}
	
	
	//admın uptade kısmı
	public void updateUserAuth(Long id , AdminUserUpdateRequest adminUserUpdateRequest) {
		boolean emailExist =userRepository.existsByEmail(adminUserUpdateRequest.getEmail());
		User user = userRepository.findById(id).orElseThrow(()-> 
		new ResourceNotFoundException(String.format(ErrorMEssage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMEssage.NOT_PERMITTED_METHOT_MESSAGE);
		}
		if(emailExist && !adminUserUpdateRequest.getEmail().equals(user.getEmail())) {
			throw new ConflictException(String.format(ErrorMEssage.EMAIL_ALREADY_EXIST, user.getEmail()));
		}
		
		if(adminUserUpdateRequest.getPassword()==null) {
			adminUserUpdateRequest.setPassword(user.getPassword());
		}
		else {
			String encodedPassword =passwordEncoder.encode(adminUserUpdateRequest.getPassword());
			adminUserUpdateRequest.setPassword(encodedPassword);
		}
		
	
		
		Set<String> userStrRoles = adminUserUpdateRequest.getRoles();
		Set<Role> roles = convertRoles(userStrRoles);
		
		User updateUser =userMapper.adminUserUpdateRequestToUser(adminUserUpdateRequest);
		updateUser.setId(user.getId());
		updateUser.setRoles(roles);
		
		userRepository.save(updateUser);
		
	}
	
	//strıng rolleron role entitylere donusturme
	public Set<Role> convertRoles(Set<String> pRoles){
		Set<Role> roles = new HashSet<>();
		if(pRoles==null) {
			Role userRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER).orElseThrow(()-> new
					ResourceNotFoundException(String.format(ErrorMEssage.ROLE_NOT_FOUND_MESSAGE, RoleType.ROLE_CUSTOMER.name())));
			roles.add(userRole);
		}
		else {
			pRoles.forEach(role->{
				switch(role) {
				case "Administrator":
					Role adminRole=roleRepository.findByName(RoleType.ROLE_ADMIN).
					orElseThrow(()->new ResourceNotFoundException(
							String.format(ErrorMEssage.ROLE_NOT_FOUND_MESSAGE, RoleType.ROLE_ADMIN.name())));
					
					roles.add(adminRole);
					break;
					
					default:
						Role userRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER).orElseThrow(()-> new
								ResourceNotFoundException(String.format(ErrorMEssage.ROLE_NOT_FOUND_MESSAGE, RoleType.ROLE_CUSTOMER.name())));
						roles.add(userRole);
				}
			});
		}
		return roles;
	}
	
	

}
