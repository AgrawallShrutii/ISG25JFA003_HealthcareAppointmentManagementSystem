package com.cognizant.hams.controller;

import com.cognizant.hams.dto.request.PatientDTO;
import com.cognizant.hams.dto.response.PatientResponseDTO;
import com.cognizant.hams.security.CustomUserDetailsService;
import com.cognizant.hams.security.JwtTokenUtil;
import com.cognizant.hams.service.NotificationService;
import com.cognizant.hams.service.impl.PatientServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientServiceImpl patientService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientDTO patientDTO;
    private PatientResponseDTO patientResponseDTO;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        patientDTO = new PatientDTO(
                "John Doe",
                LocalDate.now().minusYears(25),
                "Male",
                "1234567890",
                "john@test.com",
                "123 Test St",
                "A+"
        );
        patientResponseDTO = new PatientResponseDTO(1L, "John Doe", "john@test.com", "1234567890", "123 Test St", "Male", LocalDate.now().minusYears(25), "A+");
    }

    @Test
    // FIX 1: Add @WithMockUser to the createPatient test.
    // This provides a default authenticated user, bypassing the 401 Unauthorized error.
    @WithMockUser
    void createPatient_WithValidData_Returns201Created() throws Exception {
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(patientResponseDTO);

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

//    @Test
//    @WithMockUser
//    void getPatientById_Returns200Ok() throws Exception {
//        when(patientService.getPatientById(anyLong())).thenReturn(patientResponseDTO);
//
//        mockMvc.perform(get("/api/patients/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.patientId", is(1)));
//    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePatient_AsAdmin_Returns200Ok() throws Exception {
        when(patientService.deletePatient(1L)).thenReturn(patientResponseDTO);

        mockMvc.perform(delete("/api/patients/{patientId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

}