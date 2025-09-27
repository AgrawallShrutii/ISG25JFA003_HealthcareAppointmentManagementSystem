package com.cognizant.hams.controller;

import com.cognizant.hams.dto.request.AppointmentDTO;
import com.cognizant.hams.dto.response.AppointmentResponseDTO;
import com.cognizant.hams.service.AppointmentService;
import com.cognizant.hams.service.DoctorService;
import com.cognizant.hams.service.NotificationService;
import com.cognizant.hams.service.PatientService; // Or a dedicated AppointmentService
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AppointmentController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final NotificationService notificationService;
    private final AppointmentService appointmentService;

    @PostMapping("/patients/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentResponseDTO newAppointment = appointmentService.bookAppointment(appointmentDTO);
        return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/patients/status")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsForPatient() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsForPatient();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(
            @PathVariable("appointmentId") Long appointmentId) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable("appointmentId") Long appointmentId,
            @Valid @RequestBody AppointmentDTO appointmentUpdateDTO) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(appointmentId, appointmentUpdateDTO);
        return ResponseEntity.ok(updatedAppointment);
    }

    @PatchMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable("appointmentId") Long appointmentId) {
        AppointmentResponseDTO canceledAppointment = appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok(canceledAppointment);
    }

    @PostMapping("/doctors/appointments/{appointmentId}/confirm")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponseDTO> confirmAppointment(@PathVariable Long appointmentId){
        AppointmentResponseDTO responseDTO = appointmentService.confirmAppointment(appointmentId);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/doctors/appointments/{appointmentId}/reject")
    public ResponseEntity<AppointmentResponseDTO> rejectAppointment(@PathVariable Long appointmentId,
                                                                    @RequestParam(value = "reason", required = false) String reason){
        AppointmentResponseDTO responseDTO = appointmentService.rejectAppointment(appointmentId, reason);
        return ResponseEntity.ok(responseDTO);
    }
}