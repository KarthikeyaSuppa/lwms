package com.lwms.backend.dao;

import com.lwms.backend.entities.ReportConfigs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportConfigsRepository extends JpaRepository<ReportConfigs, Integer> {
	Optional<ReportConfigs> findByReportName(String reportName);
} 