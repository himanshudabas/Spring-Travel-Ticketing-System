package com.himanshudabas.springboot.travelticketing.service;

import com.himanshudabas.springboot.travelticketing.constant.EmployeeImplConstant;
import com.himanshudabas.springboot.travelticketing.enumeration.Role;
import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.domain.UserPrincipal;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;
import com.himanshudabas.springboot.travelticketing.model.Employee;
import com.himanshudabas.springboot.travelticketing.repository.EmployeeRepository;
import com.himanshudabas.springboot.travelticketing.service.email.EmailService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
@Qualifier("employeeDetailsService")
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailService emailService;


    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               BCryptPasswordEncoder bCryptPasswordEncoder,
                               LoginAttemptService loginAttemptService,
                               EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) {
        Employee employee = employeeRepository.findEmployeeByEmail(username);
        if (employee == null) {
            LOGGER.error(EmployeeImplConstant.NO_EMPLOYEE_FOUND_BY_USERNAME + username);
            throw new EmployeeNotFoundException(EmployeeImplConstant.NO_EMPLOYEE_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(employee);
            employeeRepository.save(employee);
            UserPrincipal userPrincipal = new UserPrincipal(employee);
            LOGGER.info("[loadUserByUsername]: " + EmployeeImplConstant.FOUND_EMPLOYEE_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    @Override
    public Employee register(Employee employee) throws UsernameExistException, EmailExistException, SendEmailFailException, UsernameEmailMismatchException {
        validateNewUsernameAndEmail(employee.getUsername(), employee.getEmail());
        Role employeeType = Role.ROLE_USER;
        Employee newEmployee = new Employee();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        newEmployee.setFirstName(employee.getFirstName());
        newEmployee.setLastName(employee.getLastName());
        newEmployee.setUsername(employee.getUsername());
        newEmployee.setEmail(employee.getEmail());
        newEmployee.setPassword(encodedPassword);
        newEmployee.setActive(true);
        newEmployee.setNotLocked(true);
        newEmployee.setRole(employeeType.name());
        newEmployee.setAuthorities(String.join(",", employeeType.getAuthorities()));
        newEmployee.setBusinessUnit(employee.getBusinessUnit());
        newEmployee.setTelephone(employee.getTelephone());
        newEmployee.setTitle(employee.getTitle());
        newEmployee.setAddress(employee.getAddress());
        employeeRepository.save(newEmployee);
        LOGGER.info("New employee password: " + password);
        emailService.send(employee.getEmail(), password);
        return newEmployee;
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        return employeeRepository.findEmployeeByUsername(username);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findEmployeeByEmail(email);
    }

    @Override
    public Employee updateEmployee(Employee employee, String username) throws EmployeeNotFoundException {
        Employee existingEmployee = validateExistingUser(username);
        if (isNotBlank(employee.getUsername())) {
            existingEmployee.setUsername(employee.getUsername());
            existingEmployee.setEmail(employee.getUsername());
        }
        if (isNotBlank(employee.getEmail())) {
            existingEmployee.setUsername(employee.getEmail());
            existingEmployee.setEmail(employee.getEmail());
        }
        if (isNotBlank(employee.getFirstName()))
            existingEmployee.setFirstName(employee.getFirstName());
        if (isNotBlank(employee.getLastName()))
            existingEmployee.setLastName(employee.getLastName());
        if (isNotBlank(employee.getBusinessUnit()))
            existingEmployee.setBusinessUnit(employee.getBusinessUnit());
        if (isNotBlank(employee.getTelephone()))
            existingEmployee.setTelephone(employee.getTelephone());
        if (isNotBlank(employee.getTitle()))
            existingEmployee.setTitle(employee.getTitle());
        if (employee.getAddress() != null)
            existingEmployee.setAddress(employee.getAddress());

        employeeRepository.save(existingEmployee);
        return existingEmployee;
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, SendEmailFailException {
        Employee employee = validateExistingEmail(email);
        String password = generatePassword();
        employee.setPassword(encodePassword(password));
        employeeRepository.save(employee);
        emailService.send(email, password);
    }

    private Employee validateExistingUser(String username) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        if (employee == null) {
            throw new EmployeeNotFoundException(EmployeeImplConstant.NO_EMPLOYEE_FOUND_BY_USERNAME + username);
        }
        return employee;
    }

    private Employee validateExistingEmail(String email) throws EmailNotFoundException {
        Employee employee = employeeRepository.findEmployeeByEmail(email);
        if (employee == null) {
            throw new EmailNotFoundException(EmployeeImplConstant.NO_EMPLOYEE_FOUND_BY_EMAIL + email);
        }
        return employee;
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private void validateNewUsernameAndEmail(String newUsername, String newEmail) throws EmailExistException, UsernameExistException, UsernameEmailMismatchException {
        Employee newEmployeeByUsername = findEmployeeByUsername(newUsername);
        Employee newEmployeeByEmail = findEmployeeByEmail(newEmail);

        if (newEmployeeByUsername != null) {
            throw new UsernameExistException(EmployeeImplConstant.USERNAME_ALREADY_EXISTS);
        }
        if (newEmployeeByEmail != null) {
            throw new EmailExistException(EmployeeImplConstant.EMAIL_ALREADY_EXISTS);
        }
        if (!newUsername.equals(newEmail)) {

            throw new UsernameEmailMismatchException("");
        }
    }

    private void validateLoginAttempt(Employee employee) {
        if (employee.isNotLocked()) {
            employee.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(employee.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(employee.getUsername());
        }
    }
}
