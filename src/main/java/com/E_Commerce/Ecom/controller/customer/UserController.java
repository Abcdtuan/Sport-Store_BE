package com.E_Commerce.Ecom.controller.customer;

import com.E_Commerce.Ecom.dto.ChangePasswordRequest;
import com.E_Commerce.Ecom.dto.UserDto;
import com.E_Commerce.Ecom.services.customer.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        if(userDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@ModelAttribute UserDto userDto) throws IOException {
        UserDto userDto1 = userService.updateUser(userDto);
        if(userDto1 == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userDto1, HttpStatus.OK);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<UserDto> changePassword(@RequestBody ChangePasswordRequest request) {
        UserDto userDto = userService.changePassword(request);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
