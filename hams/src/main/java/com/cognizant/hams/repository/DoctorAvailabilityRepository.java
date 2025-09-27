package com.cognizant.hams.repository;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cognizant.hams.entity.DoctorAvailability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorDoctorId(Long doctorId);

    boolean existsByDoctorDoctorIdAndAvailableDateAndStartTime(Long doctorId, @FutureOrPresent(message = "Availability date must be in the present or future") @NotNull(message = "Availability date is required") LocalDate availableDate, @NotNull(message = "Start time is required") LocalTime startTime);
}
