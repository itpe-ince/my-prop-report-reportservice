package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Kitchen;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Kitchen entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KitchenRepository extends JpaRepository<Kitchen, Long> {}
