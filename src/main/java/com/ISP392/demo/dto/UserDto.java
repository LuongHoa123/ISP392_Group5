package com.ISP392.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private Long roleId;
    private String roleName;
    private Integer status = 1;
}
