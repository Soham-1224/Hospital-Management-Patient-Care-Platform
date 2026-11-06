
package com.hospital.controller;

import com.hospital.dto.AppointmentResponseDTO;
import com.hospital.dto.DoctorResponseDTO;
import com.hospital.dto.PatientResponseDTO;

import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;


    // Dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {

        return ResponseEntity.ok(Map.of(
                "totalDoctors", doctorService.getTotalDoctors(),
                "totalPatients", patientService.getTotalPatients(),
                "totalAppointments", appointmentService.getTotalAppointments(),
                "pendingAppointments", appointmentService.getPendingAppointments()
        ));
    }


    // Get all doctors
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {

        List<DoctorResponseDTO> doctors =
                doctorService.getAllDoctors()
                        .stream()
                        .map(DoctorResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(doctors);
    }


    // Get all patients
    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {

        List<PatientResponseDTO> patients =
                patientService.getAllPatients()
                        .stream()
                        .map(PatientResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(patients);
    }


    // Get all appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {

        List<AppointmentResponseDTO> appointments =
                appointmentService.getAllAppointments()
                        .stream()
                        .map(AppointmentResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(appointments);
    }
}
