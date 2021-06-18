package com.himanshudabas.springboot.travelticketing.repository;

import com.himanshudabas.springboot.travelticketing.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
