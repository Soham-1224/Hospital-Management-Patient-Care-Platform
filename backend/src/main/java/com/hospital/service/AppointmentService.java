package com.hospital.service;

import com.hospital.dto.AppointmentBookingRequestDTO;
import com.hospital.dto.AppointmentNotesUpdateDTO;
import com.hospital.dto.AppointmentStatusUpdateDTO;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;

import com.hospital.entity.User;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;

import com.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;


    // Book an appointment
    public Appointment bookAppointment(
            AppointmentBookingRequestDTO request,
            String authenticatedEmail,
            boolean isAdmin) {

        Patient patient;

        if (isAdmin) {

            // Admin can select any patient.
            patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Patient not found"
                            )
                    );

        } else {

            // Patient is identified using the authenticated account.
            User user = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Authenticated user not found"
                            )
                    );

            patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Patient profile not found"
                            )
                    );
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Doctor not found"
                        )
                );


        List<Appointment.Status> activeStatuses = List.of(
                Appointment.Status.PENDING,
                Appointment.Status.CONFIRMED
        );


        LocalDateTime requestedDateTime = LocalDateTime.of(
                request.getDate(),
                request.getTime()
        );

        if (!requestedDateTime.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointments cannot be booked in the past"
            );
        }


        boolean doctorHasConflict =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                                doctor.getId(),
                                request.getDate(),
                                request.getTime(),
                                activeStatuses
                        );

        if (doctorHasConflict) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The doctor already has an appointment at this date and time"
            );
        }


        boolean patientHasConflict =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                                patient.getId(),
                                request.getDate(),
                                request.getTime(),
                                activeStatuses
                        );

        if (patientHasConflict) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have an appointment at this date and time"
            );
        }

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getDate());
        appointment.setAppointmentTime(request.getTime());
        appointment.setSymptoms(request.getSymptoms());
        appointment.setStatus(Appointment.Status.PENDING);

        return appointmentRepository.save(appointment);
    }


    public List<Appointment> getAppointmentsByPatient(
            Long patientId,
            String authenticatedEmail,
            boolean isAdmin) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Patient not found"
                        )
                );

        verifyPatientOwnership(patient, authenticatedEmail, isAdmin);

        return appointmentRepository
                .findByPatientIdOrderByAppointmentDateDesc(patientId);
    }


    public List<Appointment> getAppointmentsByDoctor(
            Long doctorId,
            String authenticatedEmail,
            boolean isAdmin) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Doctor not found"
                        )
                );

        verifyDoctorOwnership(doctor, authenticatedEmail, isAdmin);

        return appointmentRepository
                .findByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }


    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }


    public Appointment updateStatus(
            Long id,
            AppointmentStatusUpdateDTO request,
            String authenticatedEmail,
            boolean isAdmin) {

        Appointment appointment = findAppointment(id);

        // Verify that the doctor owns this appointment,
        // unless the caller is an admin.
        verifyDoctorOwnership(
                appointment.getDoctor(),
                authenticatedEmail,
                isAdmin
        );

        Appointment.Status currentStatus = appointment.getStatus();

        Appointment.Status requestedStatus;

        try {
            requestedStatus = Appointment.Status.valueOf(
                    request.getStatus().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid appointment status"
            );
        }

        // Prevent changing terminal states.
        if (currentStatus == Appointment.Status.COMPLETED
                || currentStatus == Appointment.Status.CANCELLED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A completed or cancelled appointment cannot be modified"
            );
        }

        // Only allow the transitions defined by the appointment lifecycle.
        boolean validTransition =
                (currentStatus == Appointment.Status.PENDING
                        && requestedStatus == Appointment.Status.CONFIRMED)
                        ||
                        (currentStatus == Appointment.Status.CONFIRMED
                                && requestedStatus == Appointment.Status.COMPLETED);

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid appointment status transition from "
                            + currentStatus + " to " + requestedStatus
            );
        }

        appointment.setStatus(requestedStatus);

        return appointmentRepository.save(appointment);
    }


    public Appointment addNotes(
            Long id,
            AppointmentNotesUpdateDTO request,
            String authenticatedEmail,
            boolean isAdmin) {

        Appointment appointment = findAppointment(id);

        verifyDoctorOwnership(
                appointment.getDoctor(),
                authenticatedEmail,
                isAdmin
        );

        if (appointment.getStatus() != Appointment.Status.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Notes can only be added to confirmed appointments"
            );
        }

        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.Status.COMPLETED);

        return appointmentRepository.save(appointment);
    }


    public void cancelAppointment(
            Long id,
            String authenticatedEmail,
            boolean isAdmin) {

        Appointment appointment = findAppointment(id);

        verifyPatientOwnership(
                appointment.getPatient(),
                authenticatedEmail,
                isAdmin
        );

        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed appointments cannot be cancelled"
            );
        }

        if (appointment.getStatus() == Appointment.Status.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment is already cancelled"
            );
        }

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


    private Appointment findAppointment(Long id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Appointment not found"
                        )
                );
    }


    private void verifyPatientOwnership(
            Patient patient,
            String authenticatedEmail,
            boolean isAdmin) {

        if (isAdmin) {
            return;
        }

        if (!patient.getUser().getEmail()
                .equalsIgnoreCase(authenticatedEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this patient's appointments"
            );
        }
    }


    private void verifyDoctorOwnership(
            Doctor doctor,
            String authenticatedEmail,
            boolean isAdmin) {

        if (isAdmin) {
            return;
        }

        if (!doctor.getUser().getEmail()
                .equalsIgnoreCase(authenticatedEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this doctor's appointments"
            );
        }
    }

}