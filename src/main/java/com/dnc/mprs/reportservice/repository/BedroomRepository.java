package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Bedroom;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bedroom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BedroomRepository extends JpaRepository<Bedroom, Long> {}
