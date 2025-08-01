package com.thiennd.service;

import com.thiennd.dto.SeatTypeDTO;
import com.thiennd.entity.SeatType;

import java.util.List;

public interface SeatTypeService {
    void create(SeatTypeDTO seatTypeDTO);

    SeatType findById(Long id);

    List<SeatType> findAllOrderBySeatTypeCode();

    Long countSeatType();

    void updateBooking(Long id);

    void delete(Long id);

    void duplicate(Long id);

    void update(SeatTypeDTO seatTypeDTO);
}
