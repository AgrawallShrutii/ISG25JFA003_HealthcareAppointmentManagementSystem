package com.cognizant.hams.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

// this DTO is used for the requesting body when creating a new patient
@NotBlank(message = "Name is required")
private String name;
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    private String gender;

    @Pattern(regexp = "\\d{10}", message = "Invalid contact number")
    private String contactNumber;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    private String bloodGroup;
}


