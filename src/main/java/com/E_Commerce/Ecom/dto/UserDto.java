package com.E_Commerce.Ecom.dto;

import com.E_Commerce.Ecom.enums.UserRole;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserDto {

    private Long id;

    private String email;

    private String name;

    private UserRole userRole;

    private byte[] byteImg;

    private MultipartFile img;
}
