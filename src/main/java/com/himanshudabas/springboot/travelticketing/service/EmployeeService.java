package com.himanshudabas.springboot.travelticketing.service;

import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.model.Employee;
import com.himanshudabas.springboot.travelticketing.exception.email.SendEmailFailException;

import java.util.List;

public interface EmployeeService {

    Employee register(Employee employee) throws EmployeeNotFoundException, UsernameExistException, EmailExistException, SendEmailFailException, UsernameEmailMismatchException;

    List<Employee> getEmployees();

    Employee findEmployeeByUsername(String username);

    Employee findEmployeeByEmail(String email);

    Employee updateEmployee(Employee employee, String username) throws EmployeeNotFoundException;

    void resetPassword(String email) throws EmailNotFoundException, SendEmailFailException;

}
