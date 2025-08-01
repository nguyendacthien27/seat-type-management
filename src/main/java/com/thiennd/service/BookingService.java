package com.thiennd.service;

import com.thiennd.dto.SeatTypeDTO;
import com.thiennd.entity.Booking;
import com.thiennd.entity.SeatType;

import java.util.List;

public interface BookingService {
    List<Booking> findAllByUsername(String username);
    void save(Long seatTypeId, String username);
}
