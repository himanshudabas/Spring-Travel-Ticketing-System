package com.himanshudabas.springboot.travelticketing.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "USER_SEQUENCE", sequenceName = "user_sequence", allocationSize=1)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQUENCE")
    @Column(name = "USER_ID", nullable = false, updatable = false)
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
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "ADDRESS_ID")
    @Valid
    private Address address;

    @OneToMany(mappedBy = "userId")
    private Collection<Ticket> tickets = new ArrayList<>();

}
