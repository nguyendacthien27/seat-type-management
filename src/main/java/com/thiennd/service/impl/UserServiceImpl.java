package com.thiennd.service.impl;

import com.thiennd.dto.UserDTO;
import com.thiennd.exception.UserException;
import com.thiennd.service.UserService;
import com.thiennd.service.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UserServiceImpl implements UserService {
    private final JdbcUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ValidationService validationService;

    @Autowired
    public UserServiceImpl(JdbcUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, ValidationService validationService) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.validationService = validationService;
    }

    @Override
    public void register(UserDTO userDTO) {
        if (validationService.userExists(userDTO)) {
            throw new UserException("Username already exists");
        }
        UserDetails newUser = User.builder()
                .username(StringUtils.trim(userDTO.getUsername()))
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles("USER")
                .build();

        userDetailsManager.createUser(newUser);
    }

    @Override
    public void login(UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(StringUtils.trim(userDTO.getUsername()), userDTO.getPassword())
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        httpSessionSecurityContextRepository.saveContext(context, request, response);
    }
}
