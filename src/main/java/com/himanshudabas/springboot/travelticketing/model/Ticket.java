package com.himanshudabas.springboot.travelticketing.model;

import com.himanshudabas.springboot.travelticketing.enumeration.ExpenseBorneByType;
import com.himanshudabas.springboot.travelticketing.enumeration.TicketPriorityType;
import com.himanshudabas.springboot.travelticketing.enumeration.TicketStatus;
import com.himanshudabas.springboot.travelticketing.enumeration.TicketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "TICKET_SEQUENCE", sequenceName = "ticket_sequence", allocationSize=1)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TICKET_SEQUENCE")
    @Column(name = "TICKET_ID", nullable = false, updatable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User userId;
    private TicketType type;
    private TicketPriorityType priority;
    private String travelTo;
    private String travelFrom;
    private Date startDate;
    private Date endDate;
    @Column(length = 25, nullable = false)
    private String passportNumber;
    @Column(length = 100, nullable = false)
    private String projectName;
    private ExpenseBorneByType borneBy;
    @Column(length = 100)
    private String approverName;
    @Column(length = 100)
    private String expectedDuration;
    @Column(length = 500)
    private String maxAllowedAmount;
    @Column(length = 1000, nullable = false)
    private String moreDetails;
    private TicketStatus ticketStatus;
    @Column(nullable = false, updatable = false)
    private Date submitDate;

    @OneToOne(mappedBy = "ticket", fetch = FetchType.EAGER)
    private ResolveInfo resolveInfo;

}
