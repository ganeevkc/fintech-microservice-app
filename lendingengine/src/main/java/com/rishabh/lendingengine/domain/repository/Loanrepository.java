package com.rishabh.lendingengine.domain.repository;

import com.rishabh.lendingengine.domain.model.Loan;
import com.rishabh.lendingengine.domain.model.Status;
import com.rishabh.lendingengine.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Loanrepository extends JpaRepository<Loan, Long> {

    List<Loan> findByBorrowerAndStatus(User borrower, Status status);
    List<Loan> findByLenderAndStatus(User lender,Status status);
    Optional<Loan> findOneByIdAndBorrower(Long id,User borrower);

}
