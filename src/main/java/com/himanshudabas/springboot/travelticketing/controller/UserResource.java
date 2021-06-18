package com.himanshudabas.springboot.travelticketing.controller;

import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.domain.HttpResponse;
import com.himanshudabas.springboot.travelticketing.domain.UserPrincipal;
import com.himanshudabas.springboot.travelticketing.dto.UserDto;
import com.himanshudabas.springboot.travelticketing.dto.UserLoginDto;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;
import com.himanshudabas.springboot.travelticketing.model.User;
import com.himanshudabas.springboot.travelticketing.service.UserService;
import com.himanshudabas.springboot.travelticketing.utility.JWTTokenProvider;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

import static com.himanshudabas.springboot.travelticketing.constant.EmailConstant.EMAIL_SENT_TO;
import static com.himanshudabas.springboot.travelticketing.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/user"})
public class UserResource extends ExceptionHandling {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public UserResource(AuthenticationManager authenticationManager, UserService userService, ModelMapper modelMapper, JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/allUsers")
    public ResponseEntity<Map<Long, UserDto>> getAllUsers() {
        Map<Long, UserDto> mpNew = userService.getUsers().stream()
                .collect(Collectors.toMap(User::getId, this::toDto));
        return ResponseEntity.ok(mpNew);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        LOGGER.info("[login]");
        authenticate(userLoginDto.getUsername(), userLoginDto.getPassword());
        User user = userService.findUserByUsername(userLoginDto.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(user);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(toDto(user), jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) throws UserNotFoundException, UsernameExistException, EmailExistException, SendEmailFailException, UsernameEmailMismatchException {
        LOGGER.info("[register]");
        User newUser = userService.register(toEntity(userDto));
        UserPrincipal userPrincipal = new UserPrincipal(newUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(toDto(newUser), jwtHeader, OK);
    }

    @PreAuthorize("authentication.name == #username")
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        LOGGER.info("[getUser]");
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(toDto(user), HttpStatus.OK);
    }


    @PreAuthorize("authentication.name == #username")
    @PostMapping("/update/{username}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable String username) throws UserNotFoundException {
        LOGGER.info("[updateUser]");
        User newUser = userService.updateUser(toEntity(userDto), username);
        return new ResponseEntity<>(toDto(newUser), OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email) throws EmailNotFoundException, SendEmailFailException {
        LOGGER.info("[resetPassword]");
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT_TO + email);
    }


    private ResponseEntity<HttpResponse> response(HttpStatus status, String message) {
        HttpResponse body = new HttpResponse(status.value(), status,
                status.getReasonPhrase().toUpperCase(), message);
        return new ResponseEntity<>(body, status);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private UserDto toDto(User user) {
        String role = user.getRole();
        UserDto newObj = modelMapper.map(user, UserDto.class);
        newObj.setIsAdmin(role.equalsIgnoreCase("ROLE_ADMIN"));
        return newObj;
    }

    private User toEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
