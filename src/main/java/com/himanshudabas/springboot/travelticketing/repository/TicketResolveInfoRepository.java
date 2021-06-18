package com.himanshudabas.springboot.travelticketing.repository;

import com.himanshudabas.springboot.travelticketing.model.ResolveInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketResolveInfoRepository extends JpaRepository<ResolveInfo, Long> {
}
