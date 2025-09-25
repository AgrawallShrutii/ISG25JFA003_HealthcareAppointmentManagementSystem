package com.cognizant.hams.controller;

import com.cognizant.hams.dto.Request.DoctorDTO;
import com.cognizant.hams.dto.Response.DoctorResponseDTO;
import com.cognizant.hams.security.CustomUserDetailsService;
import com.cognizant.hams.security.JwtTokenUtil;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DoctorService doctorService;

    // Mock all other dependencies of the controller and security filters
    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @WithMockUser
    public void testCreateDoctor() throws Exception {
        DoctorDTO doctorToCreate = new DoctorDTO("Dr. Strange", "MD", "Neurosurgery", "Sanctum", 10, "5551234567", "strange@sanctum.com");
        DoctorResponseDTO savedDoctor = new DoctorResponseDTO(1L, "Dr. Strange", "Neurosurgery", "MD", "Sanctum", 10, "strange@sanctum.com", "5551234567");

        Mockito.when(doctorService.createDoctor(any(com.cognizant.hams.dto.Request.AdminUserRequestDTO.class))).thenReturn(savedDoctor);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorToCreate))
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.doctorName", is("Dr. Strange")));
    }

    @Test
    @WithMockUser
    public void testGetDoctorById() throws Exception {
        DoctorResponseDTO doctor = new DoctorResponseDTO(1L, "Dr. Strange", "Neurosurgery", null, null, null, null, null);
        Mockito.when(doctorService.getDoctorById(1L)).thenReturn(doctor);

        mockMvc.perform(get("/api/doctors/{doctorId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName", is("Dr. Strange")));
    }

    @Test
    @WithMockUser
    public void testGetAllDoctor() throws Exception {
        List<DoctorResponseDTO> doctors = Collections.singletonList(
                new DoctorResponseDTO(1L, "Dr. Strange", "Neurosurgery", null, null, null, null, null)
        );
        Mockito.when(doctorService.getAllDoctor()).thenReturn(doctors);

        mockMvc.perform(get("/api/doctors/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doctorName", is("Dr. Strange")));
    }

    @Test
    @WithMockUser
    public void testUpdateDoctor() throws Exception {
        long doctorId = 1L;
        DoctorDTO doctorToUpdate = new DoctorDTO("Dr. Strange", "Sorcerer Supreme", "Neurosurgery", null, null, null, null);
        DoctorResponseDTO updatedDoctor = new DoctorResponseDTO(doctorId, "Dr. Strange", "Neurosurgery", "Sorcerer Supreme", null, null, null, null);

        Mockito.when(doctorService.updateDoctor(eq(doctorId), any(DoctorDTO.class))).thenReturn(updatedDoctor);
        mockMvc.perform(put("/api/doctors/{doctorId}", doctorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorToUpdate))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qualification", is("Sorcerer Supreme")));
    }
}