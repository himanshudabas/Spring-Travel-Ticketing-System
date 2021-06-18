package com.himanshudabas.springboot.travelticketing.dto;

import com.himanshudabas.springboot.travelticketing.enumeration.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomResolveInfoDto {
    private String comment;
    private TicketStatus ticketStatus;
    private MultipartFile documents = null;
}
