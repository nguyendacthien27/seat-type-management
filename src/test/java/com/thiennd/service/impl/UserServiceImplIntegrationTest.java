package com.thiennd.service.impl;

import com.thiennd.dto.UserDTO;
import com.thiennd.exception.UserException;
import com.thiennd.service.UserService;
import com.thiennd.service.ValidationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @MockitoBean
    private ValidationService validationService;

    private UserDTO testUser;

    @BeforeEach
    void setup() {
        testUser = new UserDTO();
        testUser.setUsername("john");
        testUser.setPassword("123456");

        when(validationService.userExists(testUser)).thenReturn(false);
    }

    @Test
    void testRegisterSuccess() {
        userService.register(testUser);

        assertTrue(userDetailsManager.userExists("john"));
    }

    @Test
    void testRegisterDuplicateThrowsException() {
        when(validationService.userExists(testUser)).thenReturn(true);

        assertThrows(UserException.class, () -> userService.register(testUser));
    }

    @Test
    void testLoginSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("john");
        userDTO.setPassword("password123");
        userService.register(userDTO);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        userService.login(userDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("john", authentication.getName());
        assertTrue(authentication.isAuthenticated());

        SecurityContext contextInSession = (SecurityContext) request.getSession()
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertNotNull(contextInSession);
        assertEquals("john", contextInSession.getAuthentication().getName());
    }
}
