package com.thiennd.controller;

import com.thiennd.service.BookingService;
import com.thiennd.service.SeatTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeatTypeService seatTypeService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private Authentication authentication;

    @Test
    void testGetBookingPage() throws Exception {
        Mockito.when(authentication.getName()).thenReturn("admin");
        Mockito.when(bookingService.findAllByUsername("admin")).thenReturn(Collections.emptyList());
        Mockito.when(seatTypeService.findAllOrderBySeatTypeCode()).thenReturn(Collections.emptyList());
        Mockito.when(seatTypeService.countSeatType()).thenReturn(0L);

        mockMvc.perform(get("/seat-type/booking").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("booking"))
                .andExpect(model().attributeExists("bookings"))
                .andExpect(model().attributeExists("seatTypes"))
                .andExpect(model().attributeExists("totalCount"));

        verify(bookingService, times(1)).findAllByUsername("admin");
        verify(seatTypeService, times(1)).findAllOrderBySeatTypeCode();
        verify(seatTypeService, times(1)).countSeatType();
    }

    @Test
    void testPostBookingSuccess() throws Exception {
        Mockito.when(authentication.getName()).thenReturn("admin");

        mockMvc.perform(post("/seat-type/booking/{id}", 1L).principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"));

        verify(seatTypeService, times(1)).updateBooking(1L);
        verify(bookingService, times(1)).save(1L, "admin");
    }

    @Test
    void testPostBookingWithException() throws Exception {
        Mockito.when(authentication.getName()).thenReturn("admin");
        doThrow(new RuntimeException("Error booking"))
                .when(seatTypeService).updateBooking(1L);

        mockMvc.perform(post("/seat-type/booking/{id}", 1L).principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"));

        verify(seatTypeService, times(1)).updateBooking(1L);
        verify(bookingService, times(0)).save(any(), any());
    }
}
