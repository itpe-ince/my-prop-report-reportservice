package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.LivingRoom;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LivingRoom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LivingRoomRepository extends JpaRepository<LivingRoom, Long> {}
