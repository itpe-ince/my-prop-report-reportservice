package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Bathroom;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bathroom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BathroomRepository extends JpaRepository<Bathroom, Long> {}
