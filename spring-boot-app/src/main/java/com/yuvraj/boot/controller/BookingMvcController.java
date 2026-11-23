package com.yuvraj.boot.controller;

import com.yuvraj.boot.entity.Booking;
import com.yuvraj.boot.entity.User;
import com.yuvraj.boot.repository.UserRepository;
import com.yuvraj.boot.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Demonstrates Task 3 — Spring MVC Architecture:
 * - @Controller stereotype
 * - Inversion of Control & Dependency Injection
 * - Model, ModelAndView, and @ModelAttribute
 * - Form submission handling and @Valid validation with BindingResult
 */
@Controller
@RequestMapping("/mvc/bookings")
public class BookingMvcController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingMvcController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    /**
     * Demonstrates Model and View: Lists all bookings.
     */
    @GetMapping
    public ModelAndView listBookings() {
        ModelAndView mav = new ModelAndView("bookings_list");
        List<Booking> list = bookingService.getAllBookings();
        mav.addObject("bookings", list);
        mav.addObject("totalCount", list.size());
        return mav;
    }

    /**
     * Demonstrates Form display with bound @ModelAttribute.
     */
    @GetMapping("/new")
    public String showBookingForm(Model model) {
        Booking booking = new Booking();
        model.addAttribute("booking", booking);
        model.addAttribute("users", userRepository.findAll());
        return "booking_form";
    }

    /**
     * Demonstrates Form Handling and Server-side Validation with @Valid & BindingResult.
     */
    @PostMapping("/submit")
    public String submitBookingForm(@Valid @ModelAttribute("booking") Booking booking,
                                    BindingResult bindingResult,
                                    @RequestParam("userId") int userId,
                                    Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "booking_form";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        booking.setUser(user);
        bookingService.createBooking(booking);

        return "redirect:/mvc/bookings?success=true";
    }
}
