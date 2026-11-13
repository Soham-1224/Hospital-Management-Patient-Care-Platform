package com.hospital.repository;

import com.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);
    long countByStatus(Appointment.Status status);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            List<Appointment.Status> statuses
    );

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            List<Appointment.Status> statuses
    );
}
