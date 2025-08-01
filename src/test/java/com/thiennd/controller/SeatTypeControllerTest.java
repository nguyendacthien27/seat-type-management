package com.thiennd.controller;

import com.thiennd.dto.SeatTypeDTO;
import com.thiennd.entity.SeatType;
import com.thiennd.service.SeatTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(SeatTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class SeatTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeatTypeService seatTypeService;

    @Test
    void testGetCreatePage() throws Exception {
        mockMvc.perform(get("/seat-type/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create"))
                .andExpect(model().attributeExists("seatType"));
    }

    @Test
    void testPostCreateSuccess() throws Exception {
        mockMvc.perform(post("/seat-type/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("seatTypeName", "VIP"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"));

        verify(seatTypeService, times(1)).create(any(SeatTypeDTO.class));
    }

    @Test
    void testPostCreateValidationError() throws Exception {
        mockMvc.perform(post("/seat-type/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("seatTypeName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("create"));

        verify(seatTypeService, times(0)).create(any(SeatTypeDTO.class));
    }

    @Test
    void testPostCreateException() throws Exception {
        doThrow(new RuntimeException("Create error")).when(seatTypeService).create(any());

        mockMvc.perform(post("/seat-type/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("seatTypeName", "VIP"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/create"))
                .andExpect(flash().attributeExists("error"));

        verify(seatTypeService, times(1)).create(any(SeatTypeDTO.class));
    }

    @Test
    void testDeleteSuccess() throws Exception {
        mockMvc.perform(post("/seat-type/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"));

        verify(seatTypeService, times(1)).delete(1L);
    }

    @Test
    void testDeleteException() throws Exception {
        doThrow(new RuntimeException("Delete error")).when(seatTypeService).delete(1L);

        mockMvc.perform(post("/seat-type/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/seat-type/booking"));
    }

    @Test
    void testDuplicateSuccess() throws Exception {
        mockMvc.perform(post("/seat-type/duplicate/{id}", 2L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"));

        verify(seatTypeService, times(1)).duplicate(2L);
    }

    @Test
    void testEditPageSuccess() throws Exception {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeName("Test");
        Mockito.when(seatTypeService.findById(1L)).thenReturn(seatType);

        mockMvc.perform(get("/seat-type/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("seatType"));
    }

    @Test
    void testEditPageException() throws Exception {
        Mockito.when(seatTypeService.findById(1L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/seat-type/edit/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(redirectedUrl("/seat-type/booking"));
    }

    @Test
    void testPostEditSuccess() throws Exception {
        mockMvc.perform(post("/seat-type/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("seatTypeName", "VIP"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"));

        verify(seatTypeService, times(1)).update(any(SeatTypeDTO.class));
    }

    @Test
    void testPostEditValidationError() throws Exception {
        mockMvc.perform(post("/seat-type/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("seatTypeName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"));

        verify(seatTypeService, times(0)).update(any(SeatTypeDTO.class));
    }


    @Test
    void testDuplicateException() throws Exception {
        doThrow(new RuntimeException("Duplicate error")).when(seatTypeService).duplicate(2L);

        mockMvc.perform(post("/seat-type/duplicate/{id}", 2L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"))
                .andExpect(flash().attribute("error", "Duplicate error"));

        verify(seatTypeService, times(1)).duplicate(2L);
    }

    @Test
    void testPostEditException() throws Exception {
        doThrow(new RuntimeException("Update error")).when(seatTypeService).update(any());

        mockMvc.perform(post("/seat-type/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("seatTypeName", "VIP"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seat-type/booking"))
                .andExpect(flash().attribute("error", "Update error"));

        verify(seatTypeService, times(1)).update(any(SeatTypeDTO.class));
    }
}
