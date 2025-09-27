package com.cognizant.hams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {
    private Long patientId;
    private String name;
    private String email;
    private String contactNumber;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
}
