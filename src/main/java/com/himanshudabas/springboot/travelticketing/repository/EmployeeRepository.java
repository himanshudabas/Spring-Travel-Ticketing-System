package com.himanshudabas.springboot.travelticketing.repository;

import com.himanshudabas.springboot.travelticketing.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findEmployeeByUsername(String username);

    Employee findEmployeeByEmail(String email);
}
