package com.example.campusconnect.repository;

import com.example.campusconnect.model.AccessAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessAlertRepository extends JpaRepository<AccessAlert, Long> {
    List<AccessAlert> findByEmailOrderByMomentDesc(String email);
    List<AccessAlert> findAllByOrderByMomentDesc();
}
