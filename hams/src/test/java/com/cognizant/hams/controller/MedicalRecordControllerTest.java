package com.cognizant.hams.controller;

import com.cognizant.hams.dto.request.MedicalRecordDTO;
import com.cognizant.hams.dto.response.MedicalRecordResponseDTO;
// FIX 1: Import the security dependencies to be mocked
import com.cognizant.hams.security.CustomUserDetailsService;
import com.cognizant.hams.security.JwtTokenUtil;
import com.cognizant.hams.service.MedicalRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordService;

    // FIX 2: Add mock beans for the security classes to allow the test context to load.
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private MedicalRecordDTO medicalRecordDTO;
    private MedicalRecordResponseDTO medicalRecordResponseDTO;

    @BeforeEach
    void setUp() {
        medicalRecordDTO = new MedicalRecordDTO(1L, 1L, 1L, "Flu", "Viral Infection", "Rest well");
        medicalRecordResponseDTO = new MedicalRecordResponseDTO();
        medicalRecordResponseDTO.setRecordId(1L);
        medicalRecordResponseDTO.setPatientName("John Patient");
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createRecord_AsDoctor_Returns201Created() throws Exception {
        when(medicalRecordService.createRecord(any(MedicalRecordDTO.class))).thenReturn(medicalRecordResponseDTO);

        mockMvc.perform(post("/api/medical-records")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recordId", is(1)));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getRecordsForPatient_AsPatient_Returns200Ok() throws Exception {
        when(medicalRecordService.getRecordsForPatient()).thenReturn(List.of(medicalRecordResponseDTO));

        mockMvc.perform(get("/api/medical-records/patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patientName", is("John Patient")));
    }
}