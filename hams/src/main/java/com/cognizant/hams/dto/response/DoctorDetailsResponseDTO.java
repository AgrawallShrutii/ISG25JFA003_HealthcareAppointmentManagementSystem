package com.cognizant.hams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetailsResponseDTO {
    private Long doctorId;
    private String contactNumber;
    private String doctorName;
    private String email;
    private String clinicAddress;

    private String specialization;

    private String qualification;
    private Integer yearOfExperience;
}
