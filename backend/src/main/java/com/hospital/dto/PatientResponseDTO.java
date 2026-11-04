package com.hospital.dto;

import com.hospital.entity.Patient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientResponseDTO {

    private Long id;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;
    private String bloodGroup;
    private String emergencyContact;
    private String medicalHistory;

    public static PatientResponseDTO fromEntity(Patient patient) {

        PatientResponseDTO dto = new PatientResponseDTO();

        dto.setId(patient.getId());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setAddress(patient.getAddress());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setMedicalHistory(patient.getMedicalHistory());

        return dto;
    }
}
