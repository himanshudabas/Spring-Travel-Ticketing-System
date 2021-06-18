package com.himanshudabas.springboot.travelticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResolveInfoDto {
    private Long ticketId;
    private String comment;
    private String adminName;
    private List<DocumentDto> documents = null;
}
