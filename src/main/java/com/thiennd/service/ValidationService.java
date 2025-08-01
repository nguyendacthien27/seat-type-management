package com.thiennd.service;

import com.thiennd.dto.UserDTO;

public interface ValidationService {
    String getNextSeatTypeCode();
    boolean userExists(UserDTO userDTO);
}
