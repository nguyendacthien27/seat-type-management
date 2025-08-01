package com.thiennd.controller;

import com.thiennd.dto.UserDTO;
import com.thiennd.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String login(Model model) {
        model.addAttribute("user", new UserDTO());
        return "login";
    }

    @PostMapping
    public String login(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            userService.login(userDTO);
            return "redirect:/seat-type/booking";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Invalid username or password!");
            return "login";
        }
    }
}
