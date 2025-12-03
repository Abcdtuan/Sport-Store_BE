package com.E_Commerce.Ecom.services.customer.user;

import com.E_Commerce.Ecom.dto.ChangePasswordRequest;
import com.E_Commerce.Ecom.dto.UserDto;

import java.io.IOException;

public interface UserService {

    UserDto getUserById(Long id);

    UserDto updateUser(UserDto userDto) throws IOException;

    UserDto changePassword(ChangePasswordRequest request);
}
