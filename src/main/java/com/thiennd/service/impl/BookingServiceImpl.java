package com.thiennd.service.impl;

import com.thiennd.entity.Booking;
import com.thiennd.entity.SeatType;
import com.thiennd.exception.SeatTypeException;
import com.thiennd.repository.BookingRepository;
import com.thiennd.repository.SeatTypeRepository;
import com.thiennd.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final SeatTypeRepository seatTypeRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, SeatTypeRepository seatTypeRepository) {
        this.bookingRepository = bookingRepository;
        this.seatTypeRepository = seatTypeRepository;
    }

    @Override
    public List<Booking> findAllByUsername(String username) {
        return bookingRepository.findAllByUsername(username);
    }

    @Override
    public void save(Long seatTypeId, String username) {
        Optional<SeatType> seatTypeOptional = seatTypeRepository.findById(seatTypeId);

        if (seatTypeOptional.isEmpty()) {
            throw new SeatTypeException("The seat type is not exists");
        }

        SeatType seatType = seatTypeOptional.get();

        Booking booking = new Booking();
        booking.setSeatType(seatType);
        booking.setUsername(username);

        bookingRepository.save(booking);
    }
}
