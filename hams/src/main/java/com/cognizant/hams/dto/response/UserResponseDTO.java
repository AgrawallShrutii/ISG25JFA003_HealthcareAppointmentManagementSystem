package com.cognizant.hams.dto.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long doctorId;
    private String doctorName;
    private String qualification;
    private String specialization;
    private String clinicAddress;
    private Integer yearOfExperience;
    private String contactNumber;
    private String email;
}