package com.hospital.dto;

import com.hospital.entity.Appointment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentResponseDTO {

    private Long id;

    private Long patientId;

    private Long doctorId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private Appointment.Status status;

    private String symptoms;

    private String notes;

    private LocalDateTime createdAt;

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {

        AppointmentResponseDTO dto = new AppointmentResponseDTO();

        dto.setId(appointment.getId());

        dto.setPatientId(appointment.getPatient().getId());

        dto.setDoctorId(appointment.getDoctor().getId());

        dto.setAppointmentDate(appointment.getAppointmentDate());

        dto.setAppointmentTime(appointment.getAppointmentTime());

        dto.setStatus(appointment.getStatus());

        dto.setSymptoms(appointment.getSymptoms());

        dto.setNotes(appointment.getNotes());

        dto.setCreatedAt(appointment.getCreatedAt());

        return dto;
    }
}