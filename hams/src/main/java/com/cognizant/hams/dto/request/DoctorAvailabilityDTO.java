package com.cognizant.hams.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class DoctorAvailabilityDTO {

    private Long availabilityId;

    private Long doctorId;

    @FutureOrPresent(message = "Availability date must be in the present or future")
    @NotNull(message = "Availability date is required")
    private LocalDate availableDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private boolean available;
}