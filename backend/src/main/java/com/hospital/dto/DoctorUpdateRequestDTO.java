package com.hospital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorUpdateRequestDTO {

    @Size(min = 2, max = 100,
            message = "Specialization must be between 2 and 100 characters")
    private String specialization;

    @Size(max = 150,
            message = "Qualification cannot exceed 150 characters")
    private String qualification;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer yearsOfExperience;

    @Size(max = 100,
            message = "Available days cannot exceed 100 characters")
    private String availableDays;

    @Size(max = 100,
            message = "Available time cannot exceed 100 characters")
    private String availableTime;

    @DecimalMin(value = "0.0",
            message = "Consultation fee cannot be negative")
    private Double consultationFee;

    @Size(max = 20,
            message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;
}
