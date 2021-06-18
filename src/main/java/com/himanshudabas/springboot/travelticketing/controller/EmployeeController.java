package com.himanshudabas.springboot.travelticketing.controller;

import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.domain.HttpResponse;
import com.himanshudabas.springboot.travelticketing.domain.UserPrincipal;
import com.himanshudabas.springboot.travelticketing.dto.EmployeeDto;
import com.himanshudabas.springboot.travelticketing.dto.EmployeeLoginDto;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;
import com.himanshudabas.springboot.travelticketing.model.Employee;
import com.himanshudabas.springboot.travelticketing.service.EmployeeService;
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
@RequestMapping(path = {"/employee"})
public class EmployeeController extends ExceptionHandling {

    private final ModelMapper modelMapper;
    private final EmployeeService employeeService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public EmployeeController(AuthenticationManager authenticationManager, EmployeeService employeeService, ModelMapper modelMapper, JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/allEmployees")
    public ResponseEntity<Map<Long, EmployeeDto>> getAllUsers() {
        Map<Long, EmployeeDto> mpNew = employeeService.getEmployees().stream()
                .collect(Collectors.toMap(Employee::getId, this::toDto));
        return ResponseEntity.ok(mpNew);
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeDto> login(@Valid @RequestBody EmployeeLoginDto employeeLoginDto) {
        LOGGER.info("[login]");
        authenticate(employeeLoginDto.getUsername(), employeeLoginDto.getPassword());
        Employee employee = employeeService.findEmployeeByUsername(employeeLoginDto.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(employee);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(toDto(employee), jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<EmployeeDto> register(@Valid @RequestBody EmployeeDto employeeDto) throws EmployeeNotFoundException, UsernameExistException, EmailExistException, SendEmailFailException, UsernameEmailMismatchException {
        LOGGER.info("[register]");
        Employee newEmployee = employeeService.register(toEntity(employeeDto));
        UserPrincipal userPrincipal = new UserPrincipal(newEmployee);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(toDto(newEmployee), jwtHeader, OK);
    }

    @PreAuthorize("authentication.name == #username")
    @GetMapping("/{username}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable String username) {
        LOGGER.info("[getUser]");
        Employee employee = employeeService.findEmployeeByUsername(username);
        return new ResponseEntity<>(toDto(employee), HttpStatus.OK);
    }


    @PreAuthorize("authentication.name == #username")
    @PostMapping("/update/{username}")
    public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto, @PathVariable String username) throws EmployeeNotFoundException {
        LOGGER.info("[updateUser]");
        Employee newEmployee = employeeService.updateEmployee(toEntity(employeeDto), username);
        return new ResponseEntity<>(toDto(newEmployee), OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email) throws EmailNotFoundException, SendEmailFailException {
        LOGGER.info("[resetPassword]");
        employeeService.resetPassword(email);
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

    private EmployeeDto toDto(Employee employee) {
        String role = employee.getRole();
        EmployeeDto newObj = modelMapper.map(employee, EmployeeDto.class);
        newObj.setIsAdmin(role.equalsIgnoreCase("ROLE_ADMIN"));
        return newObj;
    }

    private Employee toEntity(EmployeeDto employeeDto) {
        return modelMapper.map(employeeDto, Employee.class);
    }
}
