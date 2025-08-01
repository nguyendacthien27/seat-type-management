package com.thiennd.controller;

import com.thiennd.dto.SeatTypeDTO;
import com.thiennd.service.SeatTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seat-type")
public class SeatTypeController {
    private final SeatTypeService seatTypeService;

    @Autowired
    public SeatTypeController(SeatTypeService seatTypeService) {
        this.seatTypeService = seatTypeService;
    }

    @GetMapping("/create")
    public String page(Model model) {
        SeatTypeDTO seatTypeDTO = new SeatTypeDTO();
        seatTypeDTO.setSeatTypeName("New Seat Type");
        model.addAttribute("seatType", seatTypeDTO);
        return "create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("seatType") SeatTypeDTO seatTypeDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "create";
        }

        try {
            seatTypeService.create(seatTypeDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seat-type/create";
        }
        return "redirect:/seat-type/booking";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            seatTypeService.delete(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seat-type/booking";
    }

    @PostMapping("/duplicate/{id}")
    public String duplicate(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            seatTypeService.duplicate(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seat-type/booking";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        try {
            model.addAttribute("seatType", seatTypeService.findById(id));
            return "edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/seat-type/booking";
        }
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("seatType") SeatTypeDTO seatTypeDTO,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }

        try {
            seatTypeService.update(seatTypeDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seat-type/booking";
}
}