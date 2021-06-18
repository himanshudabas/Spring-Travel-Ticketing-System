package com.himanshudabas.springboot.travelticketing.controller;

import com.himanshudabas.springboot.travelticketing.dto.CustomResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.model.Document;
import com.himanshudabas.springboot.travelticketing.service.TicketService;
import com.himanshudabas.springboot.travelticketing.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping(path = {"/tickets"})
public class TicketController extends ExceptionHandling {

    private final TicketService ticketService;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // get all tickets
    @GetMapping("")
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        LOGGER.info("[getAllTickets]");
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // get ticket by Id
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable Long ticketId) throws UnauthorizedTicketAccessException, TicketNotFoundException {
        LOGGER.info("[getTicket]");
        return ResponseEntity.ok(ticketService.getTicket(ticketId));
    }

    @GetMapping("/resolveInfo/{ticketId}")
    public ResponseEntity<TicketResolveInfoDto> getTicketResolveInfo(@PathVariable Long ticketId) throws TicketResolveInfoNotFoundException, UnauthorizedTicketAccessException {
        return ResponseEntity.ok(ticketService.getTicketResolveInfo(ticketId));
    }

    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping("/resolveInfo/{ticketId}")
    public ResponseEntity<TicketResolveInfoDto> changeResolveInfo(@ModelAttribute CustomResolveInfoDto request, BindingResult result, @PathVariable Long ticketId) throws Exception {
        if (result.hasErrors()) {
            System.out.println("something bad happened");
        }
        if (request.getComment().equals("null")) {
            request.setComment("");
        }
        return ResponseEntity.ok(ticketService.changeTicketResolveInfo(ticketId, request));
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long documentId) throws DocumentNotFoundException, UnauthorizedDocumentAccessException {
        Document doc = ticketService.getDocument(documentId);
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                .body(doc.getData());
    }

    // create new ticket
    @PostMapping("/create")
    public ResponseEntity<TicketDto> createTicket(@RequestBody Ticket ticket) {
        LOGGER.info("[createTicket]");
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }

    // update existing ticket by ID
    @PostMapping("/update/{ticketId}")
    public ResponseEntity<TicketDto> updateTicket(@RequestBody Ticket ticket, @PathVariable Long ticketId) throws UnauthorizedTicketAccessException, TicketInProcessException {
        LOGGER.info("[updateTicket]");
        return ResponseEntity.ok(ticketService.updateTicket(ticket, ticketId));
    }


}
