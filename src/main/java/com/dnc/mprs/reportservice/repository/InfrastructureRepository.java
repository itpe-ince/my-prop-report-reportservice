package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Infrastructure entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long> {}
