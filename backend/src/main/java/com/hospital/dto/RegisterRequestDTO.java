package com.hospital.dto;

import com.hospital.entity.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
            message = "Name must contain between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,
            message = "Password must contain between 8 and 72 characters")
    private String password;

    @NotNull(message = "Role is required")
    private User.Role role;

    // Doctor-specific fields
    private String specialization;
    private String qualification;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer yearsOfExperience;

    @DecimalMin(value = "0.0",
            message = "Consultation fee cannot be negative")
    private Double consultationFee;

    private String availableDays;
    private String availableTime;

    // Patient-specific fields
    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String phoneNumber;
}
