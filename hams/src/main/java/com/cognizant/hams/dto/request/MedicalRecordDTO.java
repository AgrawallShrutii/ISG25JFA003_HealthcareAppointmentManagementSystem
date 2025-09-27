package com.cognizant.hams.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    @NotNull
    private Long appointmentId;
    @NotNull
    private Long patientId;
    @NotNull
    private Long doctorId;
    private String reason;
    private String diagnosis;
    private String notes;
}