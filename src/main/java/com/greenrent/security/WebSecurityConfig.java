package com.greenrent.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity.IgnoredRequestConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.greenrent.security.jwt.AuthTokenFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class WebSecurityConfig {
	
	
	private UserDetailsService userDetailsService;
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		
		 authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	
		
		
		
        http.csrf().disable().sessionManagement().
        sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().
        
        authorizeHttpRequests().antMatchers(HttpMethod.OPTIONS,"/**").permitAll().and()
        
        .authorizeHttpRequests().antMatchers("/register", "/login" ,"/car/visitors/all","/car/visitors/pages", 
        		"/files/display/**","/contactmessage/visitor","/actuator/**","/car/visitors/{id}").permitAll().
        anyRequest().authenticated();
        
        
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
		return http.build();
		
	}
	
	
	
	
	private static final String [] AUTH_WHITE_LIST = {
			"/v3/api-docs/**",
			"swagger-ui.html",
			"/swagger-ui/**",
			"/",
			"index.html",
			"/images/**"
			
	};
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		 return (web) -> web.ignoring().antMatchers(AUTH_WHITE_LIST);
	}
	

	
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	
	@Bean

	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	    return authenticationConfiguration.getAuthenticationManager();
	}
	
	/*

	@Bean

	public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
	    return (AuthenticationManager) auth;
	}
	*/
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
}
