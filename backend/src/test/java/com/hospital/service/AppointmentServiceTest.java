
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment createAppointment(Appointment.Status status) {

        User patientUser = new User();
        patientUser.setId(10L);
        patientUser.setEmail("patient@test.com");
        patientUser.setRole(User.Role.PATIENT);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUser(patientUser);

        User doctorUser = new User();
        doctorUser.setId(20L);
        doctorUser.setEmail("doctor@test.com");
        doctorUser.setRole(User.Role.DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setUser(doctorUser);

        Appointment appointment = new Appointment();
        appointment.setId(100L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(status);

        return appointment;
    }

    private AppointmentBookingRequestDTO createBookingRequest() {

        AppointmentBookingRequestDTO request =
                new AppointmentBookingRequestDTO();

        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setDate(LocalDate.now().plusDays(1));
        request.setTime(LocalTime.of(10, 0));
        request.setSymptoms("Regular health checkup");

        return request;
    }

    private void mockBookingDependencies() {

        User patientUser = new User();
        patientUser.setId(10L);
        patientUser.setEmail("patient@test.com");
        patientUser.setRole(User.Role.PATIENT);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUser(patientUser);

        Doctor doctor = new Doctor();
        doctor.setId(2L);

        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(Optional.of(patientUser));

        when(patientRepository.findByUserId(10L))
                .thenReturn(Optional.of(patient));

        when(doctorRepository.findById(2L))
                .thenReturn(Optional.of(doctor));
    }

    private AppointmentNotesUpdateDTO createNotesRequest() {

        AppointmentNotesUpdateDTO request =
                new AppointmentNotesUpdateDTO();

        request.setNotes("Patient examined. Treatment completed.");

        return request;
    }

    @Test
    void shouldCancelPendingAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(
                100L, "patient@test.com", false
        );

        assertEquals(
                Appointment.Status.CANCELLED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldCancelConfirmedAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.CONFIRMED);

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(
                100L, "patient@test.com", false
        );

        assertEquals(
                Appointment.Status.CANCELLED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldNotCancelCompletedAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.COMPLETED);

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.cancelAppointment(
                        100L, "patient@test.com", false
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotCancelAlreadyCancelledAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.CANCELLED);

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.cancelAppointment(
                        100L, "patient@test.com", false
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldConfirmPendingAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        AppointmentStatusUpdateDTO request =
                new AppointmentStatusUpdateDTO();
        request.setStatus("CONFIRMED");

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.updateStatus(
                100L, request, "doctor@test.com", false
        );

        assertEquals(
                Appointment.Status.CONFIRMED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldCompleteConfirmedAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.CONFIRMED);

        AppointmentStatusUpdateDTO request =
                new AppointmentStatusUpdateDTO();
        request.setStatus("COMPLETED");

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.updateStatus(
                100L, request, "doctor@test.com", false
        );

        assertEquals(
                Appointment.Status.COMPLETED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldRejectInvalidStatusTransition() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        AppointmentStatusUpdateDTO request =
                new AppointmentStatusUpdateDTO();
        request.setStatus("COMPLETED");

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.updateStatus(
                        100L, request, "doctor@test.com", false
                )
        );

        assertEquals(
                Appointment.Status.PENDING,
                appointment.getStatus()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowUpdatingCompletedAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.COMPLETED);

        AppointmentStatusUpdateDTO request =
                new AppointmentStatusUpdateDTO();
        request.setStatus("CONFIRMED");

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.updateStatus(
                        100L, request, "doctor@test.com", false
                )
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowPatientToCancelAnotherPatientsAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.cancelAppointment(
                        100L, "anotherpatient@test.com", false
                )
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode()
        );

        assertEquals(
                Appointment.Status.PENDING,
                appointment.getStatus()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowDoctorToUpdateAnotherDoctorsAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        AppointmentStatusUpdateDTO request =
                new AppointmentStatusUpdateDTO();
        request.setStatus("CONFIRMED");

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.updateStatus(
                        100L, request, "anotherdoctor@test.com", false
                )
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode()
        );

        assertEquals(
                Appointment.Status.PENDING,
                appointment.getStatus()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldBookAppointmentSuccessfully() {

        mockBookingDependencies();

        AppointmentBookingRequestDTO request =
                createBookingRequest();

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(false);

        when(appointmentRepository
                .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.bookAppointment(
                request, "patient@test.com", false
        );

        assertNotNull(result);
        assertEquals(1L, result.getPatient().getId());
        assertEquals(2L, result.getDoctor().getId());
        assertEquals(
                Appointment.Status.PENDING,
                result.getStatus()
        );

        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldRejectBookingWhenDoctorHasConflictingAppointment() {

        mockBookingDependencies();

        AppointmentBookingRequestDTO request =
                createBookingRequest();

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.bookAppointment(
                        request, "patient@test.com", false
                )
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldRejectBookingWhenPatientHasConflictingAppointment() {

        mockBookingDependencies();

        AppointmentBookingRequestDTO request =
                createBookingRequest();

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(false);

        when(appointmentRepository
                .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.bookAppointment(
                        request, "patient@test.com", false
                )
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldRejectAppointmentScheduledInPast() {

        mockBookingDependencies();

        AppointmentBookingRequestDTO request =
                createBookingRequest();

        request.setDate(LocalDate.now().minusDays(1));

        assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.bookAppointment(
                        request, "patient@test.com", false
                )
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldCheckOnlyActiveAppointmentsForBookingConflicts() {

        mockBookingDependencies();

        AppointmentBookingRequestDTO request =
                createBookingRequest();

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(false);

        when(appointmentRepository
                .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                        anyLong(), any(LocalDate.class),
                        any(LocalTime.class), anyList()))
                .thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.bookAppointment(
                request, "patient@test.com", false
        );

        List<Appointment.Status> expectedStatuses = List.of(
                Appointment.Status.PENDING,
                Appointment.Status.CONFIRMED
        );

        verify(appointmentRepository).existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                eq(2L),
                eq(request.getDate()),
                eq(request.getTime()),
                eq(expectedStatuses)
        );

        verify(appointmentRepository).existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusIn(
                eq(1L),
                eq(request.getDate()),
                eq(request.getTime()),
                eq(expectedStatuses)
        );
    }

    @Test
    void shouldAllowDoctorToAddNotesToConfirmedAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.CONFIRMED);

        AppointmentNotesUpdateDTO request = createNotesRequest();

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.addNotes(
                100L, request, "doctor@test.com", false
        );

        assertEquals(
                "Patient examined. Treatment completed.",
                appointment.getNotes()
        );

        assertEquals(
                Appointment.Status.COMPLETED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldRejectAddingNotesToPendingAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        AppointmentNotesUpdateDTO request = createNotesRequest();

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.addNotes(
                        100L, request, "doctor@test.com", false
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertEquals(
                Appointment.Status.PENDING,
                appointment.getStatus()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldRejectNotesFromAnotherDoctor() {

        Appointment appointment =
                createAppointment(Appointment.Status.CONFIRMED);

        AppointmentNotesUpdateDTO request = createNotesRequest();

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.addNotes(
                        100L, request, "anotherdoctor@test.com", false
                )
        );

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode()
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldAllowAdminToCancelAnyPatientsAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(
                100L, "admin@test.com", true
        );

        assertEquals(
                Appointment.Status.CANCELLED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldAllowAdminToUpdateAnyDoctorsAppointment() {

        Appointment appointment =
                createAppointment(Appointment.Status.PENDING);

        AppointmentStatusUpdateDTO request =
                new AppointmentStatusUpdateDTO();

        request.setStatus("CONFIRMED");

        when(appointmentRepository.findById(100L))
                .thenReturn(Optional.of(appointment));

        appointmentService.updateStatus(
                100L, request, "admin@test.com", true
        );

        assertEquals(
                Appointment.Status.CONFIRMED,
                appointment.getStatus()
        );

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() {

        when(appointmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> appointmentService.cancelAppointment(
                        999L, "patient@test.com", false
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(appointmentRepository, never()).save(any());
    }
}

