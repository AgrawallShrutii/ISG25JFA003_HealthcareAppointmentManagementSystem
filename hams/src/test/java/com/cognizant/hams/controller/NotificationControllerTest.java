package com.cognizant.hams.controller;

import com.cognizant.hams.dto.response.NotificationResponseDTO;
import com.cognizant.hams.security.CustomUserDetailsService;
import com.cognizant.hams.security.JwtTokenUtil;
import com.cognizant.hams.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    // FIX: Add mock beans for the security dependencies required by your security filter.
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @WithMockUser(roles = "PATIENT")
    void getPatientNotifications_AsPatient_Returns200Ok() throws Exception {
        when(notificationService.getNotificationForPatient()).thenReturn(List.of(new NotificationResponseDTO()));

        mockMvc.perform(get("/api/notifications/patients/notification"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getNotificationsForDoctor_AsDoctor_Returns200Ok() throws Exception {
        when(notificationService.getNotificationForDoctor()).thenReturn(List.of(new NotificationResponseDTO()));

        mockMvc.perform(get("/api/notifications/doctors/notification"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    void markAsRead_WhenCalled_Returns200Ok() throws Exception {
        doNothing().when(notificationService).markAsRead(1L);

        mockMvc.perform(put("/api/notifications/{notificationId}/read", 1L)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(1L);
    }
}