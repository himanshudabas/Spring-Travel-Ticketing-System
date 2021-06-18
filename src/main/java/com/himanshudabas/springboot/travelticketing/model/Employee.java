package com.himanshudabas.springboot.travelticketing.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name="EMPLOYEE")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "EMPLOYEE_SEQUENCE", sequenceName = "EMPLOYEE_sequence", allocationSize=1)
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPLOYEE_SEQUENCE")
    @Column(name = "EMPLOYEE_ID", nullable = false, updatable = false)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private String businessUnit;
    private String title;
    private String password;
    private String telephone;
    private String role;

    public String[] getAuthorities() {
        return authorities.split(",");
    }

    private String authorities;
    private boolean isActive;
    private boolean isNotLocked;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ADDRESS_ID")
    @Valid
    private Address address;

    @OneToMany(mappedBy = "employeeId")
    private Collection<Ticket> tickets = new ArrayList<>();

}
