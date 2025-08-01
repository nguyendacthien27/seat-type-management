package com.thiennd.service.impl;

import com.thiennd.entity.Booking;
import com.thiennd.entity.SeatType;
import com.thiennd.exception.SeatTypeException;
import com.thiennd.repository.BookingRepository;
import com.thiennd.repository.SeatTypeRepository;
import com.thiennd.service.BookingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatTypeRepository seatTypeRepository;

    private SeatType savedSeatType;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        seatTypeRepository.deleteAll();

        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("S1");
        seatType.setSeatTypeName("VIP");
        savedSeatType = seatTypeRepository.save(seatType);
    }

    @Test
    void testSaveBookingSuccess() {
        bookingService.save(savedSeatType.getId(), "john_doe");

        List<Booking> bookings = bookingRepository.findAll();
        assertEquals(1, bookings.size());
        assertEquals("john_doe", bookings.get(0).getUsername());
        assertEquals("VIP", bookings.get(0).getSeatType().getSeatTypeName());
    }

    @Test
    void testSaveBookingSeatTypeNotExistsThrowsException() {
        assertThrows(SeatTypeException.class, () -> bookingService.save(999L, "userX"));
        assertTrue(bookingRepository.findAll().isEmpty());
    }

    @Test
    void testFindAllByUsernameReturnsCorrectBookings() {
        bookingService.save(savedSeatType.getId(), "alice");

        List<Booking> aliceBookings = bookingService.findAllByUsername("alice");

        assertEquals(1, aliceBookings.size());
        assertTrue(aliceBookings.stream().allMatch(b -> b.getUsername().equals("alice")));
    }
}
