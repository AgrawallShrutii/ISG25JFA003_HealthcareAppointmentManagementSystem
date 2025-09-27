package com.cognizant.hams.controller;

import com.cognizant.hams.dto.request.AdminUserRequestDTO;
import com.cognizant.hams.dto.response.DoctorResponseDTO;
import com.cognizant.hams.security.CustomUserDetailsService;
import com.cognizant.hams.security.JwtTokenUtil;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.NotificationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private DoctorResponseDTO doctorResponseDTO;
    private AdminUserRequestDTO adminUserRequestDTO;

    @BeforeEach
    void setUp() {
        doctorResponseDTO = new DoctorResponseDTO(1L, "Dr. Smith", "Cardiology", "MD", "Main St", 10, "smith@test.com", "1234567890");

        // FIX 1: Create a VALID AdminUserRequestDTO with all required fields filled in.
        adminUserRequestDTO = new AdminUserRequestDTO();
        adminUserRequestDTO.setDoctorName("Dr. Smith");
        adminUserRequestDTO.setSpecialization("Cardiology");
        adminUserRequestDTO.setQualification("MD");
        adminUserRequestDTO.setClinicAddress("123 Health St.");
        adminUserRequestDTO.setYearOfExperience(15);
        adminUserRequestDTO.setContactNumber("1112223333");
        adminUserRequestDTO.setEmail("smith@clinic.com");
        // Set other required fields if any (username, password, etc.)
        adminUserRequestDTO.setUsername("drsmith");
        adminUserRequestDTO.setPassword("password123");
        adminUserRequestDTO.setRoleName("ROLE_DOCTOR");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDoctor_WhenAdmin_Returns201Created() throws Exception {
        when(doctorService.createDoctor(any(AdminUserRequestDTO.class))).thenReturn(doctorResponseDTO);

        mockMvc.perform(post("/api/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUserRequestDTO)))
                .andExpect(status().isCreated()) // This should now pass with a valid DTO
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.doctorName", is("Dr. Smith")));
    }


    @Test
    @WithMockUser(roles = "DOCTOR")
    void getDoctor_WhenDoctor_Returns200Ok() throws Exception {
        when(doctorService.getDoctor()).thenReturn(doctorResponseDTO);

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName", is("Dr. Smith")));
    }

    @Test
    // FIX 2: Add @WithMockUser to this test to bypass the 401 Unauthorized error.
    // Any role will do since it's a public endpoint after authentication.
    @WithMockUser
    void getAllDoctor_WhenCalled_Returns200Ok() throws Exception {
        when(doctorService.getAllDoctor()).thenReturn(List.of(doctorResponseDTO));

        mockMvc.perform(get("/api/doctors/get-all-doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doctorName", is("Dr. Smith")));
    }
}