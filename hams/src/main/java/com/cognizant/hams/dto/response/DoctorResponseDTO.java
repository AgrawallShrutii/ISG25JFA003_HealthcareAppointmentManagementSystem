package com.cognizant.hams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTO {
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String qualification;
    private String clinicAddress;
    private Integer yearOfExperience;
    private String email;
    private String contactNumber;
}
