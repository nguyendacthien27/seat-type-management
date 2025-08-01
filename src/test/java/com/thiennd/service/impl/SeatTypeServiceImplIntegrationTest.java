package com.thiennd.service.impl;

import com.thiennd.dto.SeatTypeDTO;
import com.thiennd.entity.SeatType;
import com.thiennd.exception.SeatTypeException;
import com.thiennd.repository.SeatTypeRepository;
import com.thiennd.service.SeatTypeService;
import com.thiennd.service.ValidationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SeatTypeServiceImplIntegrationTest {

    @Autowired
    private SeatTypeService seatTypeService;

    @Autowired
    private SeatTypeRepository seatTypeRepository;

    @MockitoBean
    private ValidationService validationService;

    @BeforeEach
    void setup() {
        when(validationService.getNextSeatTypeCode()).thenReturn("A1");
    }

    @Test
    void testCreateAndFindById() {
        SeatTypeDTO dto = new SeatTypeDTO();
        dto.setSeatTypeName("VIP");
        dto.setWorkerMemo("Near window");

        seatTypeService.create(dto);

        List<SeatType> seatTypes = seatTypeRepository.findAll();
        assertEquals(1, seatTypes.size());
        SeatType seatType = seatTypes.get(0);

        SeatType found = seatTypeService.findById(seatType.getId());
        assertEquals("VIP", found.getSeatTypeName());
        assertFalse(found.isDeleted());
        assertFalse(found.isBooked());
    }

    @Test
    void testFindByIdNotExistsThrowsException() {
        assertThrows(SeatTypeException.class, () -> seatTypeService.findById(999L));
    }

    @Test
    void testUpdateBooking() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("B1");
        seatType.setSeatTypeName("Normal");
        seatTypeRepository.save(seatType);

        seatTypeService.updateBooking(seatType.getId());

        SeatType updated = seatTypeRepository.findById(seatType.getId()).orElseThrow();
        assertTrue(updated.isBooked());
    }

    @Test
    void testDeleteWhenNotBooked() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("C1");
        seatType.setSeatTypeName("Standard");
        seatTypeRepository.save(seatType);

        seatTypeService.delete(seatType.getId());

        SeatType deleted = seatTypeRepository.findById(seatType.getId()).orElseThrow();
        assertTrue(deleted.isDeleted());
    }

    @Test
    void testDeleteWhenBookedThrowsException() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("D1");
        seatType.setSeatTypeName("Standard");
        seatType.setBooked(true);
        seatTypeRepository.save(seatType);

        assertThrows(SeatTypeException.class, () -> seatTypeService.delete(seatType.getId()));
    }

    @Test
    void testDuplicateCreatesNewRecord() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("E1");
        seatType.setSeatTypeName("Economy");
        seatType.setWorkerMemo("Extra info");
        seatTypeRepository.save(seatType);

        when(validationService.getNextSeatTypeCode()).thenReturn("E2");
        seatTypeService.duplicate(seatType.getId());

        assertEquals(2, seatTypeRepository.count());
    }

    @Test
    void testUpdateSeatType() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("F1");
        seatType.setSeatTypeName("Basic");
        seatTypeRepository.save(seatType);

        SeatTypeDTO dto = new SeatTypeDTO();
        dto.setId(seatType.getId());
        dto.setSeatTypeName("Basic Updated");
        dto.setWorkerMemo("Updated memo");

        seatTypeService.update(dto);

        SeatType updated = seatTypeRepository.findById(seatType.getId()).orElseThrow();
        assertEquals("Basic Updated", updated.getSeatTypeName());
        assertEquals("Updated memo", updated.getWorkerMemo());
    }

    @Test
    void testFindAllOrderBySeatTypeCode() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("F2");
        seatType.setSeatTypeName("Basic");
        seatType.setWorkerMemo("Extra info");

        SeatType seatType1 = new SeatType();
        seatType1.setSeatTypeCode("F1");
        seatType1.setSeatTypeName("Basic");
        seatType1.setWorkerMemo("Extra info");
        seatTypeRepository.saveAll(Arrays.asList(seatType, seatType1));

        List<SeatType> results = seatTypeService.findAllOrderBySeatTypeCode();
        assertEquals(2, results.size());
        assertEquals("F1", results.get(0).getSeatTypeCode());
        assertEquals("F2", results.get(1).getSeatTypeCode());
    }

    @Test
    void testCountSeatType() {
        SeatType st1 = new SeatType();
        st1.setSeatTypeCode("X1");
        st1.setSeatTypeName("TypeX");

        SeatType st2 = new SeatType();
        st2.setSeatTypeCode("X2");
        st2.setSeatTypeName("TypeY");
        st2.setDeleted(true);

        seatTypeRepository.saveAll(List.of(st1, st2));

        Long count = seatTypeService.countSeatType();
        assertEquals(1L, count);
    }
}
