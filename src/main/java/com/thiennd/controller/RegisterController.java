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
    @RequestMapping("/register")
    public class RegisterController {
        private final UserService userService;

        @Autowired
        public RegisterController(UserService userService) {
            this.userService = userService;
        }

        @GetMapping
        public String register(Model model) {
            model.addAttribute("register", new UserDTO());
            return "register";
        }

        @PostMapping
        public String register(@Valid @ModelAttribute("register") UserDTO userDTO,
                               BindingResult bindingResult, Model model) {
            if (bindingResult.hasErrors()) {
                return "register";
            }

            try {
                userService.register(userDTO);
                return "redirect:/login";
            } catch (Exception e) {
                model.addAttribute("errorMsg", e.getMessage());
                return "register";
            }
        }
    }
