package com.hospital.dto;

import com.hospital.entity.Doctor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoctorResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String specialization;
    private String qualification;
    private Integer yearsOfExperience;
    private String availableDays;
    private String availableTime;
    private Double consultationFee;

    public static DoctorResponseDTO fromEntity(Doctor doctor) {

        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getYearsOfExperience(),
                doctor.getAvailableDays(),
                doctor.getAvailableTime(),
                doctor.getConsultationFee()
        );
    }
}