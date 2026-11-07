package com.hospital.controller;

import com.hospital.dto.PatientResponseDTO;
import com.hospital.dto.PatientUpdateRequestDTO;
import com.hospital.entity.Patient;
import com.hospital.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<?> getPatientById(@PathVariable Long id, Authentication authentication) {
            Patient patient = patientService.getPatientById(
                    id,
                    authentication.getName(),
                    isAdmin(authentication)
            );
            return ResponseEntity.ok(PatientResponseDTO.fromEntity(patient));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPatientByUserId(@PathVariable Long userId, Authentication authentication) {
            Patient patient = patientService.getPatientByUserId(
                    userId,
                    authentication.getName(),
                    isAdmin(authentication)
            );
            return ResponseEntity.ok(PatientResponseDTO.fromEntity(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequestDTO request,
            Authentication authentication) {

            Patient patient = patientService.updatePatient(
                    id,
                    request,
                    authentication.getName(),
                    isAdmin(authentication)
            );

            return ResponseEntity.ok(PatientResponseDTO.fromEntity(patient));
    }

    private boolean isAdmin(Authentication authentication) {

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}