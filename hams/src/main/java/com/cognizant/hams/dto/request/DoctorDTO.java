package com.cognizant.hams.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Clinic address is required")
    private String clinicAddress;

    @NotNull(message = "Year of experience is required")
    private Integer yearOfExperience;

    @Pattern(regexp = "\\d{10}", message = "Contact number must be 10 digits")
    private String contactNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;
}
