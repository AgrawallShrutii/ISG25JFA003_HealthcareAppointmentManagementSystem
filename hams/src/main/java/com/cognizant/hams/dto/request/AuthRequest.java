package com.cognizant.hams.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthRequest {
    private String username;
    private String password;

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
    private String email;

    //   private String status;

    @NotBlank(message = "Address is required")
    private String address;

    private String bloodGroup;

}