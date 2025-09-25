package com.cognizant.hams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "patients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"contactNumber"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Past
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