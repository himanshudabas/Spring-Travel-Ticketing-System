package com.himanshudabas.springboot.travelticketing.dto;

import com.himanshudabas.springboot.travelticketing.enumeration.TicketStatus;
import com.himanshudabas.springboot.travelticketing.enumeration.ExpenseBorneByType;
import com.himanshudabas.springboot.travelticketing.enumeration.TicketPriorityType;
import com.himanshudabas.springboot.travelticketing.enumeration.TicketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    private Long id;
    private Long employeeId;
    private TicketType type;
    private TicketPriorityType priority;
    private String travelTo;
    private String travelFrom;
    private Date startDate;
    private Date endDate;
    private String passportNumber;
    private String projectName;
    private ExpenseBorneByType borneBy;
    private String approverName;
    private String expectedDuration;
    private String maxAllowedAmount;
    private String moreDetails;
    private Date submitDate;
    private TicketStatus ticketStatus;
    private String userName;
}
