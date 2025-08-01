package com.thiennd.service.impl;

import com.thiennd.dto.UserDTO;
import com.thiennd.exception.SeatTypeException;
import com.thiennd.repository.SeatTypeRepository;
import com.thiennd.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {
    private final SeatTypeRepository seatTypeRepository;
    private final JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    public ValidationServiceImpl(SeatTypeRepository seatTypeRepository, JdbcUserDetailsManager jdbcUserDetailsManager) {
        this.seatTypeRepository = seatTypeRepository;
        this.jdbcUserDetailsManager = jdbcUserDetailsManager;
    }

    @Override
    public String getNextSeatTypeCode() {
        return seatTypeRepository.findTopByOrderBySeatTypeCodeDesc()
                .map(last -> {
                    int current = Integer.parseInt(last.getSeatTypeCode());

                    if (current >= 99) {
                        throw new SeatTypeException("Cannot create new seat type. Maximum code limit reached (99).");
                    }

                    return String.format("%02d", current + 1);
                }).orElse("01");
    }

    @Override
    public boolean userExists(UserDTO userDTO) {
        return jdbcUserDetailsManager.userExists(userDTO.getUsername());
    }
}
