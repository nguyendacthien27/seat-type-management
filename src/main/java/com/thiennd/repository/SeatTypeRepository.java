package com.thiennd.repository;

import com.thiennd.entity.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Long> {
    Optional<SeatType> findTopByOrderBySeatTypeCodeDesc();
    long countByDeletedFalse();
}
