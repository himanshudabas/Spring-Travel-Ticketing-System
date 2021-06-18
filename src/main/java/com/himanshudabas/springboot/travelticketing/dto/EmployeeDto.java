package com.himanshudabas.springboot.travelticketing.dto;

import com.himanshudabas.springboot.travelticketing.constant.DataValidationConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto {

    @NotBlank(message = DataValidationConstant.USERNAME_REQUIRED)
    String username;

    @NotBlank(message = DataValidationConstant.EMAIL_REQUIRED)
    @Email(message = DataValidationConstant.INVALID_EMAIL)
    String email;
    @NotBlank(message = DataValidationConstant.FIRSTNAME_REQUIRED)
    String firstName;
    @NotBlank(message = DataValidationConstant.LASTNAME_REQUIRED)
    String lastName;
    @NotBlank(message = DataValidationConstant.BUSINESS_UNIT_REQUIRED)
    String businessUnit;
    @NotBlank(message = DataValidationConstant.TITLE_REQUIRED)
    String title;

    @NotBlank(message = DataValidationConstant.TELEPHONE_REQUIRED)
    @Size(max = 15, message = DataValidationConstant.INVALID_TELEPHONE_LENGTH)
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = DataValidationConstant.INVALID_TELEPHONE_FORMAT)
    String telephone;

    @Valid
    AddressDto address;
    Boolean isAdmin;
}
