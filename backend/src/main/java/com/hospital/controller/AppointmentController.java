
package com.hospital.controller;

import com.hospital.dto.AppointmentBookingRequestDTO;
import com.hospital.dto.AppointmentNotesUpdateDTO;
import com.hospital.dto.AppointmentResponseDTO;
import com.hospital.dto.AppointmentStatusUpdateDTO;

import com.hospital.entity.Appointment;
import com.hospital.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Book an appointment
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody AppointmentBookingRequestDTO request,
            Authentication authentication) {

            Appointment appointment =
                    appointmentService.bookAppointment(request, authentication.getName(), isAdmin(authentication));

            return ResponseEntity.ok(
                    AppointmentResponseDTO.fromEntity(appointment)
            );
    }

    // Get appointments by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getPatientAppointments(
            @PathVariable Long patientId,
            Authentication authentication) {

        List<AppointmentResponseDTO> appointments =
                appointmentService.getAppointmentsByPatient(patientId, authentication.getName(), isAdmin(authentication))
                        .stream()
                        .map(AppointmentResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(appointments);
    }

    // Get appointments by doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getDoctorAppointments(
            @PathVariable Long doctorId,
            Authentication authentication) {

        List<AppointmentResponseDTO> appointments =
                appointmentService.getAppointmentsByDoctor(doctorId, authentication.getName(), isAdmin(authentication))
                        .stream()
                        .map(AppointmentResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(appointments);
    }

    // Get all appointments
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {

        List<AppointmentResponseDTO> appointments =
                appointmentService.getAllAppointments()
                        .stream()
                        .map(AppointmentResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(appointments);
    }

    // Update appointment status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateDTO request,
            Authentication authentication) {

            Appointment appointment =
                    appointmentService.updateStatus(id, request, authentication.getName(), isAdmin(authentication));

            return ResponseEntity.ok(
                    AppointmentResponseDTO.fromEntity(appointment)
            );
    }

    // Add doctor's notes
    @PutMapping("/{id}/notes")
    public ResponseEntity<?> addNotes(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentNotesUpdateDTO request,
            Authentication authentication) {

            Appointment appointment =
                    appointmentService.addNotes(id, request, authentication.getName(), isAdmin(authentication));

            return ResponseEntity.ok(
                    AppointmentResponseDTO.fromEntity(appointment)
            );
    }

    // Cancel appointment
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, Authentication authentication) {

            appointmentService.cancelAppointment(id, authentication.getName(), isAdmin(authentication));

            return ResponseEntity.ok("Appointment cancelled");
    }

    private boolean isAdmin(Authentication authentication) {

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}
