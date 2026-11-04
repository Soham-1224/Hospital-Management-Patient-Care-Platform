package com.hospital.service;

import com.hospital.dto.AppointmentBookingRequestDTO;
import com.hospital.dto.AppointmentNotesUpdateDTO;
import com.hospital.dto.AppointmentStatusUpdateDTO;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;

import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;


    // Book an appointment
    public Appointment bookAppointment(AppointmentBookingRequestDTO request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getDate());
        appointment.setAppointmentTime(request.getTime());
        appointment.setSymptoms(request.getSymptoms());
        appointment.setStatus(Appointment.Status.PENDING);

        return appointmentRepository.save(appointment);
    }


    // Get appointments by patient
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository
                .findByPatientIdOrderByAppointmentDateDesc(patientId);
    }


    // Get appointments by doctor
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository
                .findByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }


    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }


    // Update appointment status
    public Appointment updateStatus(Long id, AppointmentStatusUpdateDTO request) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(
                Appointment.Status.valueOf(request.getStatus().toUpperCase())
        );

        return appointmentRepository.save(appointment);
    }


    // Add doctor's notes
    public Appointment addNotes(Long id, AppointmentNotesUpdateDTO request) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.Status.COMPLETED);

        return appointmentRepository.save(appointment);
    }


    // Cancel appointment
    public void cancelAppointment(Long id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.CANCELLED);

        appointmentRepository.save(appointment);
    }


    // Total appointments
    public long getTotalAppointments() {
        return appointmentRepository.count();
    }


    // Pending appointments
    public long getPendingAppointments() {
        return appointmentRepository.countByStatus(Appointment.Status.PENDING);
    }
}