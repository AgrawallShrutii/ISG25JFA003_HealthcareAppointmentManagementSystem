package com.cognizant.hams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Date;
import java.sql.Time;


@Data
@AllArgsConstructor
public class DoctorAndAvailabilityResponseDTO {
    private Long doctorId;
    private String contactNumber;
    private String doctorName;
    private String email;
    private String clinicAddress;

    private String specialization;

    private String qualification;
    private Integer yearOfExperience;
    private Time startTime;
    private Time endTime;
    private Date availableDate;
    private boolean available;
}
