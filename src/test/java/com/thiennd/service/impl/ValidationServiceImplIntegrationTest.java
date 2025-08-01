package com.thiennd.service.impl;

import com.thiennd.dto.UserDTO;
import com.thiennd.entity.SeatType;
import com.thiennd.exception.SeatTypeException;
import com.thiennd.repository.SeatTypeRepository;
import com.thiennd.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ValidationServiceImplIntegrationTest {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private SeatTypeRepository seatTypeRepository;

    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testGetNextSeatTypeCodeWhenEmptyShouldReturn01() {
        String nextCode = validationService.getNextSeatTypeCode();
        assertEquals("01", nextCode);
    }

    @Test
    void testGetNextSeatTypeCodeWhenLastIs05ShouldReturn06() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("05");
        seatType.setSeatTypeName("Standard");
        seatTypeRepository.save(seatType);

        String nextCode = validationService.getNextSeatTypeCode();
        assertEquals("06", nextCode);
    }

    @Test
    void testGetNextSeatTypeCodeThrowsExceptionWhenMax99() {
        SeatType seatType = new SeatType();
        seatType.setSeatTypeCode("99");
        seatType.setSeatTypeName("VIP");
        seatTypeRepository.save(seatType);

        assertThrows(SeatTypeException.class, () -> validationService.getNextSeatTypeCode());
    }

    @Test
    void testUserExistsWhenUserNotExistShouldReturnFalse() {
        UserDTO dto = new UserDTO();
        dto.setUsername("unknown");
        assertFalse(validationService.userExists(dto));
    }

    @Test
    void testUserExistsWhenUserExistsShouldReturnTrue() {
        org.springframework.security.core.userdetails.User.UserBuilder builder =
                org.springframework.security.core.userdetails.User.withUsername("john")
                        .password(passwordEncoder.encode("123456"))
                        .roles("USER");

        jdbcUserDetailsManager.createUser(builder.build());

        UserDTO dto = new UserDTO();
        dto.setUsername("john");

        assertTrue(validationService.userExists(dto));
    }
}
