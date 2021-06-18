package com.himanshudabas.springboot.travelticketing.dto;

import com.himanshudabas.springboot.travelticketing.constant.DataValidationConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class AddressDto {

    @NotBlank(message = DataValidationConstant.ADDRESS1_REQUIRED)
    String address1;
    String address2;
    @NotBlank(message = DataValidationConstant.CITY_REQUIRED)
    String city;
    @NotBlank(message = DataValidationConstant.STATE_REQUIRED)
    String state;
    @NotBlank(message = DataValidationConstant.ZIPCODE_REQUIRED)
    String zipCode;
    @NotBlank(message = DataValidationConstant.COUNTRY_REQUIRED)
    String country;
}
