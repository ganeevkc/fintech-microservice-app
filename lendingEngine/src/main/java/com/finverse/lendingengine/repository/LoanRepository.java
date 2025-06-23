package com.finverse.lendingengine.repository;

import com.finverse.lendingengine.model.Loan;
import com.finverse.lendingengine.model.Status;
import com.finverse.lendingengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByUserId(UUID userId);
    List<Loan> findByBorrowerAndStatus(User borrower, Status status);
    List<Loan> findByLenderAndStatus(User lender, Status status);
    Optional<Loan> findOneByIdAndBorrower(UUID id, User borrower);

    // Additional useful methods
    List<Loan> findByBorrower(User borrower);
    List<Loan> findByLender(User lender);
    List<Loan> findByStatus(Status status);
}
