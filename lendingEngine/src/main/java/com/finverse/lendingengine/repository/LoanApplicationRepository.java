package com.finverse.lendingengine.repository;

import com.finverse.lendingengine.model.LoanApplication;
import com.finverse.lendingengine.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication,Long> {
    List<LoanApplication> findAllByStatusEquals(Status status);
}
