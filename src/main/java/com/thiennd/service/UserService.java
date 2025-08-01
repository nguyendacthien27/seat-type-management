package com.thiennd.service;

import com.thiennd.dto.UserDTO;

public interface UserService {
    void register(UserDTO userDTO);
    void login(UserDTO userDTO);
}
