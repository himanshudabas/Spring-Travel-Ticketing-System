package com.himanshudabas.springboot.travelticketing.service;

import com.himanshudabas.springboot.travelticketing.dto.CustomResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.model.Document;
import com.himanshudabas.springboot.travelticketing.model.Ticket;

import java.util.List;

public interface TicketService {
    List<TicketDto> getAllTickets();

    TicketDto getTicket(Long ticketId) throws UnauthorizedTicketAccessException, TicketNotFoundException;

    TicketDto createTicket(Ticket ticket);

    TicketDto updateTicket(Ticket updateTicket, Long ticketId) throws UnauthorizedTicketAccessException, TicketInProcessException;

    TicketResolveInfoDto getTicketResolveInfo(Long ticketId) throws UnauthorizedTicketAccessException, TicketResolveInfoNotFoundException;

    TicketResolveInfoDto changeTicketResolveInfo(Long ticketId, CustomResolveInfoDto request) throws Exception;

    Document getDocument(Long documentId) throws DocumentNotFoundException, UnauthorizedDocumentAccessException;
}
