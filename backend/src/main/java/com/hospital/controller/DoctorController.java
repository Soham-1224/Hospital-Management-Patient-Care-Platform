package com.hospital.controller;

import com.hospital.dto.DoctorResponseDTO;
import com.hospital.dto.DoctorUpdateRequestDTO;
import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {

        List<DoctorResponseDTO> doctors =
                doctorService.getAllDoctors()
                        .stream()
                        .map(DoctorResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(
            @PathVariable Long id) {

        Doctor doctor = doctorService.getDoctorById(id);

        return ResponseEntity.ok(
                DoctorResponseDTO.fromEntity(doctor)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorResponseDTO> getDoctorByUserId(
            @PathVariable Long userId) {

        Doctor doctor = doctorService.getDoctorByUserId(userId);

        return ResponseEntity.ok(
                DoctorResponseDTO.fromEntity(doctor)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(
            @RequestParam String specialization) {

        List<DoctorResponseDTO> doctors =
                doctorService.getDoctorsBySpecialization(specialization)
                        .stream()
                        .map(DoctorResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequestDTO request,
            Authentication authentication) {

        Doctor doctor = doctorService.updateDoctor(
                id,
                request,
                authentication.getName(),
                isAdmin(authentication)
        );

        return ResponseEntity.ok(DoctorResponseDTO.fromEntity(doctor));
    }

    private boolean isAdmin(Authentication authentication) {

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}