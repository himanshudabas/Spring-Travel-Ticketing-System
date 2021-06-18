package com.himanshudabas.springboot.travelticketing.dto;

import com.himanshudabas.springboot.travelticketing.constant.DataValidationConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDto {

    @Email(message = DataValidationConstant.INVALID_EMAIL)
    @NotBlank(message = DataValidationConstant.USERNAME_REQUIRED)
    String username;

    @NotBlank(message = DataValidationConstant.PASSWORD_REQUIRED)
    String password;
}
