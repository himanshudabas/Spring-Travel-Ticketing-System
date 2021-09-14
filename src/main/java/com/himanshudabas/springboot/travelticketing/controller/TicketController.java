package com.himanshudabas.springboot.travelticketing.controller;

import com.himanshudabas.springboot.travelticketing.dto.CustomResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.model.Document;
import com.himanshudabas.springboot.travelticketing.model.Ticket;
import com.himanshudabas.springboot.travelticketing.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Slf4j
@RestController
@RequestMapping(path = {"/tickets"})
public class TicketController extends ExceptionHandling {

    private final TicketService ticketService;

    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // get all tickets
    @GetMapping("")
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        log.info("[getAllTickets]");
        List<TicketDto> ticketDto = ticketService.getAllTickets();
        return ResponseEntity.ok(ticketDto);
    }

    // get ticket by Id
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable Long ticketId) throws UnauthorizedTicketAccessException, TicketNotFoundException {
        log.info("[getTicket]");
        return ResponseEntity.ok(ticketService.getTicket(ticketId));
    }

    @GetMapping("/resolveInfo/{ticketId}")
    public ResponseEntity<TicketResolveInfoDto> getTicketResolveInfo(@PathVariable Long ticketId) throws TicketResolveInfoNotFoundException, UnauthorizedTicketAccessException {
        log.info("inside getTicketResolveInfo()");
        return ResponseEntity.ok(ticketService.getTicketResolveInfo(ticketId));
    }

    @PreAuthorize("hasAuthority('admin:update')")
    @PutMapping("/resolveInfo/{ticketId}")
    public ResponseEntity<TicketResolveInfoDto> changeResolveInfo(@ModelAttribute CustomResolveInfoDto request, BindingResult result, @PathVariable Long ticketId) throws Exception {
        log.info("inside changeResolveInfo()");
        if (result.hasErrors()) {
            System.out.println("something bad happened");
        }
        if (request.getComment().equals("null")) {
            request.setComment("");
        }
        TicketResolveInfoDto ticketResolveInfoDto = ticketService.changeTicketResolveInfo(ticketId, request);
        return ResponseEntity.ok(ticketResolveInfoDto);
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long documentId) throws DocumentNotFoundException, UnauthorizedDocumentAccessException {
        log.info("inside getFile()");
        Document doc = ticketService.getDocument(documentId);
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                .body(doc.getData());
    }

    // create new ticket
    @PostMapping("/create")
    public ResponseEntity<TicketDto> createTicket(@RequestBody Ticket ticket) {
        log.info("inside createTicket()");
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }

    // update existing ticket by ID
    @PostMapping("/update/{ticketId}")
    public ResponseEntity<TicketDto> updateTicket(@RequestBody Ticket ticket, @PathVariable Long ticketId) throws UnauthorizedTicketAccessException, TicketInProcessException {
        log.info("inside updateTicket()");
        return ResponseEntity.ok(ticketService.updateTicket(ticket, ticketId));
    }


}
