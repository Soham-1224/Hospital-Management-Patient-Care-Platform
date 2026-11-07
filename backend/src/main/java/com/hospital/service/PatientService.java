package com.hospital.service;

import com.hospital.dto.PatientUpdateRequestDTO;
import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(
            Long id,
            String authenticatedEmail,
            boolean isAdmin) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Patient not found"
                        )
                );

        verifyOwnership(patient, authenticatedEmail, isAdmin);

        return patient;
    }


    public Patient getPatientByUserId(
            Long userId,
            String authenticatedEmail,
            boolean isAdmin) {

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Patient profile not found"
                        )
                );

        verifyOwnership(patient, authenticatedEmail, isAdmin);

        return patient;
    }


    public Patient updatePatient(
            Long id,
            PatientUpdateRequestDTO updatedPatient,
            String authenticatedEmail,
            boolean isAdmin) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Patient not found"
                        )
                );

        verifyOwnership(patient, authenticatedEmail, isAdmin);

        if (updatedPatient.getPhoneNumber() != null)
            patient.setPhoneNumber(updatedPatient.getPhoneNumber());

        if (updatedPatient.getAddress() != null)
            patient.setAddress(updatedPatient.getAddress());

        if (updatedPatient.getBloodGroup() != null)
            patient.setBloodGroup(updatedPatient.getBloodGroup());

        if (updatedPatient.getEmergencyContact() != null)
            patient.setEmergencyContact(updatedPatient.getEmergencyContact());

        if (updatedPatient.getMedicalHistory() != null)
            patient.setMedicalHistory(updatedPatient.getMedicalHistory());

        return patientRepository.save(patient);
    }


    private void verifyOwnership(
            Patient patient,
            String authenticatedEmail,
            boolean isAdmin) {

        if (isAdmin) {
            return;
        }

        if (!patient.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this patient profile"
            );
        }
    }

    public long getTotalPatients() {
        return patientRepository.count();
    }
}