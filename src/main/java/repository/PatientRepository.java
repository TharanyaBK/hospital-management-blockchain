package com.hospital.hospitalmanagement.repository;

import com.hospital.hospitalmanagement.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Custom query methods (if any) can go here
}
