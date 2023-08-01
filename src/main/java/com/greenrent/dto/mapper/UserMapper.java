package com.greenrent.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.greenrent.domain.User;
import com.greenrent.dto.UserDTO;
import com.greenrent.dto.request.AdminUserUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	List<UserDTO> map(List<User> user);
	//tam olarak user listesini alacak userdto lara donusturecek
	
	//*****
	//UserService 3. yorumdan sonrası için
	UserDTO userToUserDTO(User user);
	
	@Mapping(target="id" , ignore = true)
	@Mapping(target="roles" , ignore = true)
	User adminUserUpdateRequestToUser(AdminUserUpdateRequest request);
	
}
