package com.hospital.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentBookingRequestDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Appointment date is required")
    private LocalDate date;

    @NotNull(message = "Appointment time is required")
    private LocalTime time;

    @Size(max = 5000,
            message = "Symptoms cannot exceed 5000 characters")
    private String symptoms;
}