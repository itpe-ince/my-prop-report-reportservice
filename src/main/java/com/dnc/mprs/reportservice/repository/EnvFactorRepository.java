package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.EnvFactor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EnvFactor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnvFactorRepository extends JpaRepository<EnvFactor, Long> {}
