package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentStatusUpdateDTO {

    @NotBlank(message = "Appointment status is required")
    @Pattern(
            regexp = "^(?i:PENDING|CONFIRMED|COMPLETED|CANCELLED)$",
            message = "Status must be PENDING, CONFIRMED, COMPLETED, or CANCELLED"
    )
    private String status;
}
