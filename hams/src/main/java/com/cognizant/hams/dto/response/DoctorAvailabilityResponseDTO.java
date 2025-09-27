package com.cognizant.hams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityResponseDTO {
    private Long availabilityId;
    private DoctorDetailsResponseDTO doctor;
    private LocalDate availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
}
