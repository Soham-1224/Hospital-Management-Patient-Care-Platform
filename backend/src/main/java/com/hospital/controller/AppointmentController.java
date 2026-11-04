
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
            @Valid @RequestBody AppointmentBookingRequestDTO request) {

        try {
            Appointment appointment =
                    appointmentService.bookAppointment(request);

            return ResponseEntity.ok(
                    AppointmentResponseDTO.fromEntity(appointment)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get appointments by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getPatientAppointments(
            @PathVariable Long patientId) {

        List<AppointmentResponseDTO> appointments =
                appointmentService.getAppointmentsByPatient(patientId)
                        .stream()
                        .map(AppointmentResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(appointments);
    }

    // Get appointments by doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getDoctorAppointments(
            @PathVariable Long doctorId) {

        List<AppointmentResponseDTO> appointments =
                appointmentService.getAppointmentsByDoctor(doctorId)
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
            @Valid @RequestBody AppointmentStatusUpdateDTO request) {

        try {
            Appointment appointment =
                    appointmentService.updateStatus(id, request);

            return ResponseEntity.ok(
                    AppointmentResponseDTO.fromEntity(appointment)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add doctor's notes
    @PutMapping("/{id}/notes")
    public ResponseEntity<?> addNotes(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentNotesUpdateDTO request) {

        try {
            Appointment appointment =
                    appointmentService.addNotes(id, request);

            return ResponseEntity.ok(
                    AppointmentResponseDTO.fromEntity(appointment)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cancel appointment
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {

        try {
            appointmentService.cancelAppointment(id);

            return ResponseEntity.ok("Appointment cancelled");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
