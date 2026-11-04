package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentNotesUpdateDTO {

    @NotBlank(message = "Notes cannot be empty")
    @Size(max = 5000,
            message = "Notes cannot exceed 5000 characters")
    private String notes;
}
