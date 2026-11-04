package com.hospital.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientUpdateRequestDTO {

    @Pattern(
            regexp = "^[0-9+\\-() ]{7,20}$",
            message = "Enter a valid phone number"
    )
    private String phoneNumber;

    @Size(max = 255,
            message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(
            regexp = "^(A|B|AB|O)[+-]$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Enter a valid blood group"
    )
    private String bloodGroup;

    @Size(max = 20,
            message = "Emergency contact cannot exceed 20 characters")
    private String emergencyContact;

    @Size(max = 5000,
            message = "Medical history cannot exceed 5000 characters")
    private String medicalHistory;
}