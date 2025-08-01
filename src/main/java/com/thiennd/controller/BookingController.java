package com.thiennd.controller;

import com.thiennd.service.BookingService;
import com.thiennd.service.SeatTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seat-type")
public class BookingController {
    private final SeatTypeService seatTypeService;
    private final BookingService bookingService;

    @Autowired
    public BookingController(SeatTypeService seatTypeService, BookingService bookingService) {
        this.seatTypeService = seatTypeService;
        this.bookingService = bookingService;
    }

    @GetMapping("/booking")
    public String booking(Model model, Authentication authentication) {
        model.addAttribute("bookings", bookingService.findAllByUsername(authentication.getName()));
        model.addAttribute("seatTypes", seatTypeService.findAllOrderBySeatTypeCode());
        model.addAttribute("totalCount", seatTypeService.countSeatType());
        return "booking";
    }

    @PostMapping("/booking/{id}")
    public String booking(@PathVariable("id") Long seatTypeId, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            seatTypeService.updateBooking(seatTypeId);
            bookingService.save(seatTypeId, username);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seat-type/booking";
    }
}