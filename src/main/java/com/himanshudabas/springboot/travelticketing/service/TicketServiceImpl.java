package com.himanshudabas.springboot.travelticketing.service;

import com.himanshudabas.springboot.travelticketing.dto.CustomResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.dto.DocumentDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketDto;
import com.himanshudabas.springboot.travelticketing.dto.TicketResolveInfoDto;
import com.himanshudabas.springboot.travelticketing.enumeration.TicketStatus;
import com.himanshudabas.springboot.travelticketing.exception.domain.*;
import com.himanshudabas.springboot.travelticketing.model.Document;
import com.himanshudabas.springboot.travelticketing.model.ResolveInfo;
import com.himanshudabas.springboot.travelticketing.repository.TicketResolveInfoRepository;
import com.himanshudabas.springboot.travelticketing.model.Ticket;
import com.himanshudabas.springboot.travelticketing.model.User;
import com.himanshudabas.springboot.travelticketing.repository.DocumentRepository;
import com.himanshudabas.springboot.travelticketing.repository.TicketRepository;
import com.himanshudabas.springboot.travelticketing.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TicketResolveInfoRepository ticketResolveInfoRepository;
    private final DocumentRepository documentRepository;


    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository, ModelMapper modelMapper, TicketResolveInfoRepository ticketResolveInfoRepository, DocumentRepository documentRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.ticketResolveInfoRepository = ticketResolveInfoRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public List<TicketDto> getAllTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        List<TicketDto> allTickets;
        if (!isAdmin) {
            User user = userRepository.findUserByUsername(authentication.getName());
            allTickets = ticketRepository.findTicketsByUserId(user).stream().map(this::toTicketDto).collect(Collectors.toList());
        } else {
            allTickets = ticketRepository.findAll().stream().map(this::toTicketDto).collect(Collectors.toList());
        }
        return allTickets;
    }

    @Override
    public TicketDto getTicket(Long ticketId) throws UnauthorizedTicketAccessException, TicketNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return toTicketDto(checkTicketAuth(authentication.getName(), ticketId));
        } else {
            Optional<Ticket> origTicket = ticketRepository.findById(ticketId);
            if (origTicket.isEmpty()) {
                throw new TicketNotFoundException("");
            }
            return toTicketDto(origTicket.get());
        }
    }

    @Override
    public TicketDto createTicket(Ticket ticket) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUsername(authentication.getName());
        ticket.setSubmitDate(new Date());
        ticket.setTicketStatus(TicketStatus.SUBMITTED);
        ticket.setUserId(user);
        this.createTicketResolveInfo(ticket);
        return toTicketDto(ticketRepository.save(ticket));
    }

    private void createTicketResolveInfo(Ticket ticket) {
        ticketResolveInfoRepository.save(new ResolveInfo(ticket));
    }

    @Override
    public TicketDto updateTicket(Ticket newTicketDetails, Long ticketId) throws UnauthorizedTicketAccessException, TicketInProcessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Ticket newTicket = checkTicketAuth(authentication.getName(), ticketId);
        if (newTicket.getTicketStatus().equals(TicketStatus.INPROCESS)) {
            throw new TicketInProcessException("");
        }
        copyTicketDetails(newTicket, newTicketDetails);
        return toTicketDto(ticketRepository.save(newTicket));
    }

    @Override
    public TicketResolveInfoDto getTicketResolveInfo(Long ticketId) throws UnauthorizedTicketAccessException, TicketResolveInfoNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            checkTicketAuth(authentication.getName(), ticketId);
        }
        Optional<ResolveInfo> resolveInfo = ticketResolveInfoRepository.findById(ticketId);
        if (resolveInfo.isEmpty()) {
            throw new TicketResolveInfoNotFoundException("");
        }
        return toTicketResolveInfoDto(resolveInfo.get());
    }

    @Override
    public TicketResolveInfoDto changeTicketResolveInfo(Long ticketId, CustomResolveInfoDto request) throws Exception {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        ResolveInfo resolveInfo = ticketResolveInfoRepository.getOne(ticketId);
        boolean changed = false;
        if (ticket.orElseThrow(() -> new TicketNotFoundException("")).getTicketStatus() != request.getTicketStatus()) {
            // means the status has changed. so need to update the ticket status in DB
            Ticket newTicket = ticket.get();
            newTicket.setTicketStatus(request.getTicketStatus());
            ticketRepository.save(newTicket);
            changed = true;
        }
        if (request.getDocuments() != null) {
            // if document is uploaded then save it
            Document newDoc = getNewDocument(request.getDocuments(), resolveInfo);
            documentRepository.save(newDoc);
            changed = true;
        }
        if (!request.getComment().equals(resolveInfo.getComment())) {
            // if comment has changed save it

            User admin = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            resolveInfo.setAdmin(admin);
            changed = false;

            resolveInfo.setComment(request.getComment());
            return toTicketResolveInfoDto(ticketResolveInfoRepository.save(resolveInfo));
        }
        if (changed) {
            User admin = userRepository.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            resolveInfo.setAdmin(admin);
            return toTicketResolveInfoDto(ticketResolveInfoRepository.save(resolveInfo));
        }
        throw new NoChangeInResolveInfoException("");
    }

    @Override
    public Document getDocument(Long documentId) throws DocumentNotFoundException, UnauthorizedDocumentAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        Document myDoc = documentRepository.findById(documentId).orElseThrow(() -> new DocumentNotFoundException(""));
        if (isAdmin) {
            return myDoc;
        }
        Long ticketId = myDoc.getResolveInfo().getId();
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new DocumentNotFoundException(""));
        String currUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currUsername.equals(ticket.getUserId().getUsername())) {
            // user doesn't have permission to access this document
            throw new UnauthorizedDocumentAccessException("");
        }
        return myDoc;
    }

    private Document getNewDocument(MultipartFile document, ResolveInfo resolveInfo) throws IOException {
        String filename = FilenameUtils.getBaseName(document.getOriginalFilename());
        String type = FilenameUtils.getExtension(document.getOriginalFilename());
        Long size = document.getSize();
        byte[] data = document.getBytes();

        Document newDoc = new Document();
        newDoc.setType(type);
        newDoc.setData(data);
        ;
        newDoc.setName(filename);
        newDoc.setSize(size);
        newDoc.setResolveInfo(resolveInfo);
        return newDoc;
    }

    private Ticket checkTicketAuth(String username, Long ticketId) throws UnauthorizedTicketAccessException {
        User user = userRepository.findUserByUsername(username);
        Optional<Ticket> origTicket = ticketRepository.findById(ticketId);
        if (origTicket.isEmpty() || !origTicket.get().getUserId().equals(user)) {
            throw new UnauthorizedTicketAccessException("");
        }
        return origTicket.get();
    }

    private void copyTicketDetails(Ticket origTicket, Ticket updateTicket) {
        origTicket.setApproverName(updateTicket.getApproverName());
        origTicket.setBorneBy(updateTicket.getBorneBy());
        origTicket.setEndDate(updateTicket.getEndDate());
        origTicket.setMoreDetails(updateTicket.getMoreDetails());
        origTicket.setExpectedDuration(updateTicket.getExpectedDuration());
        origTicket.setPriority(updateTicket.getPriority());
        origTicket.setMaxAllowedAmount(updateTicket.getMaxAllowedAmount());
        origTicket.setType(updateTicket.getType());
        origTicket.setTravelTo(updateTicket.getTravelTo());
        origTicket.setTravelFrom(updateTicket.getTravelFrom());
        origTicket.setStartDate(updateTicket.getStartDate());
        origTicket.setProjectName(updateTicket.getProjectName());
        origTicket.setPassportNumber(updateTicket.getPassportNumber());
    }

    private TicketDto toTicketDto(Ticket ticket) {
        Long userId = ticket.getUserId().getId();
        String userName = ticket.getUserId().getFirstName() + " " + ticket.getUserId().getLastName();
        TicketDto temp = modelMapper.map(ticket, TicketDto.class);
        temp.setUserId(userId);
        temp.setUserName(userName);
        return temp;
    }

    private TicketResolveInfoDto toTicketResolveInfoDto(ResolveInfo resolveInfo) {
        User admin = resolveInfo.getAdmin();
        String adminName = "";
        if (admin != null) {
            adminName = admin.getFirstName() + " " + admin.getLastName();
        }
        TicketResolveInfoDto temp = modelMapper.map(resolveInfo, TicketResolveInfoDto.class);
        temp.setAdminName(adminName);
        return temp;
    }

    private DocumentDto toDocumentDto(Document document) {
        return modelMapper.map(document, DocumentDto.class);
    }

}
