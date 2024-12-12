package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Entrance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Entrance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntranceRepository extends JpaRepository<Entrance, Long> {}
