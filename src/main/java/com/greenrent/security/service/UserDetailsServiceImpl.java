package com.greenrent.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.greenrent.domain.User;
import com.greenrent.exception.message.ErrorMEssage;
import com.greenrent.repository.UserRepository;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private UserRepository userRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user =userRepository.findByEmail(email).
		orElseThrow(()-> new 
				UsernameNotFoundException(String.format(ErrorMEssage.USER_NOT_FOUND_MESSAGE, email)));
		
		return UserDetailsImpl.build(user);
	}

}
