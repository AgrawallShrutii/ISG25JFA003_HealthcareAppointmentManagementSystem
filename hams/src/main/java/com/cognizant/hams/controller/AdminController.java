package com.cognizant.hams.controller;

import com.cognizant.hams.dto.Request.AdminUserRequestDTO;
import com.cognizant.hams.dto.Response.UserResponseDTO;
import com.cognizant.hams.entity.Doctor;
import com.cognizant.hams.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    // Only users with the ADMIN role can access this endpoint
    // AdminController.java

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createPrivilegedUser(@RequestBody AdminUserRequestDTO request) {
        Doctor createdUser = authService.createPrivilegedUser(request);
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setDoctorId(createdUser.getDoctorId());
        responseDTO.setDoctorName(createdUser.getDoctorName());
        responseDTO.setEmail(createdUser.getEmail());
        responseDTO.setClinicAddress(createdUser.getClinicAddress());
        responseDTO.setQualification(createdUser.getQualification());
        responseDTO.setSpecialization(createdUser.getSpecialization());
        responseDTO.setContactNumber(createdUser.getContactNumber());
        responseDTO.setYearOfExperience(createdUser.getYearOfExperience());


        return ResponseEntity.ok(responseDTO);
    }
}