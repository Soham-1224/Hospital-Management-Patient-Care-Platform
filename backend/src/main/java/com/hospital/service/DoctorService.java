package com.hospital.service;

import com.hospital.dto.DoctorUpdateRequestDTO;
import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DoctorService {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private UserRepository userRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
    }

    public Doctor updateDoctor(
            Long id,
            DoctorUpdateRequestDTO updatedDoctor,
            String authenticatedEmail,
            boolean isAdmin) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Doctor not found"
                        )
                );

        verifyOwnership(doctor, authenticatedEmail, isAdmin);

        if (updatedDoctor.getSpecialization() != null)
            doctor.setSpecialization(updatedDoctor.getSpecialization());

        if (updatedDoctor.getQualification() != null)
            doctor.setQualification(updatedDoctor.getQualification());

        if (updatedDoctor.getYearsOfExperience() != null)
            doctor.setYearsOfExperience(
                    updatedDoctor.getYearsOfExperience());

        if (updatedDoctor.getAvailableDays() != null)
            doctor.setAvailableDays(updatedDoctor.getAvailableDays());

        if (updatedDoctor.getAvailableTime() != null)
            doctor.setAvailableTime(updatedDoctor.getAvailableTime());

        if (updatedDoctor.getConsultationFee() != null)
            doctor.setConsultationFee(updatedDoctor.getConsultationFee());

        if (updatedDoctor.getPhoneNumber() != null)
            doctor.setPhoneNumber(updatedDoctor.getPhoneNumber());

        return doctorRepository.save(doctor);
    }


    private void verifyOwnership(
            Doctor doctor,
            String authenticatedEmail,
            boolean isAdmin) {

        if (isAdmin) {
            return;
        }

        if (!doctor.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to update this doctor profile"
            );
        }
    }

    public long getTotalDoctors() {
        return doctorRepository.count();
    }
}
