package com.himanshudabas.springboot.travelticketing.repository;

import com.himanshudabas.springboot.travelticketing.model.Employee;
import com.himanshudabas.springboot.travelticketing.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findTicketsByEmployeeId(Employee employee);

}
