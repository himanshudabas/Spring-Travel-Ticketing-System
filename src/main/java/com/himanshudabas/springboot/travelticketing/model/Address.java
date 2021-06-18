package com.himanshudabas.springboot.travelticketing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ADDRESS")
@SequenceGenerator(name = "ADDRESS_SEQUENCE", sequenceName = "address_sequence", allocationSize=1)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDRESS_SEQUENCE")
    @Column(name = "ADDRESS_ID")
    private long addressId;

    @Column(name = "ADDRESS1", nullable = false)
    private String address1;

    @Column(name = "ADDRESS2")
    private String address2;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "STATE", nullable = false)
    private String state;

    @Column(name = "ZIPCODE", nullable = false)
    private String zipCode;

    @Column(name = "COUNTRY", nullable = false)
    private String country;


}
