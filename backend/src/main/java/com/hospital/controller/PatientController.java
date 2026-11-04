package com.hospital.controller;

import com.hospital.dto.PatientResponseDTO;
import com.hospital.dto.PatientUpdateRequestDTO;
import com.hospital.entity.Patient;
import com.hospital.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {

        List<PatientResponseDTO> patients = patientService.getAllPatients()
                .stream()
                .map(PatientResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        try {
            Patient patient = patientService.getPatientById(id);

            return ResponseEntity.ok(
                    PatientResponseDTO.fromEntity(patient)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPatientByUserId(@PathVariable Long userId) {
        try {
            Patient patient = patientService.getPatientByUserId(userId);

            return ResponseEntity.ok(
                    PatientResponseDTO.fromEntity(patient)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequestDTO request) {

        try {
            Patient updatedPatient = patientService.updatePatient(id, request);

            return ResponseEntity.ok(
                    PatientResponseDTO.fromEntity(updatedPatient)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}