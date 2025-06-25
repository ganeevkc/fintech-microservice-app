package com.finverse.lendingengine.repository;

import com.finverse.lendingengine.model.Loan;
import com.finverse.lendingengine.model.Status;
import com.finverse.lendingengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, String> {

    @Query("SELECT l FROM Loan l WHERE l.userId = :userId")
    List<Loan> findByUserId(@Param("userId") String userId);

    // Fix these queries with explicit JOIN FETCH to avoid lazy loading issues
    @Query("SELECT l FROM Loan l JOIN FETCH l.borrower WHERE l.borrower = :borrower AND l.status = :status")
    List<Loan> findByBorrowerAndStatus(@Param("borrower") User borrower, @Param("status") Status status);

    @Query("SELECT l FROM Loan l JOIN FETCH l.lender WHERE l.lender = :lender AND l.status = :status")
    List<Loan> findByLenderAndStatus(@Param("lender") User lender, @Param("status") Status status);

    @Query("SELECT l FROM Loan l JOIN FETCH l.borrower WHERE l.id = :id AND l.borrower = :borrower")
    Optional<Loan> findOneByIdAndBorrower(@Param("id") String id, @Param("borrower") User borrower);

    // Additional useful methods
    @Query("SELECT l FROM Loan l JOIN FETCH l.borrower WHERE l.borrower = :borrower")
    List<Loan> findByBorrower(@Param("borrower") User borrower);

    @Query("SELECT l FROM Loan l JOIN FETCH l.lender WHERE l.lender = :lender")
    List<Loan> findByLender(@Param("lender") User lender);

    List<Loan> findByStatus(Status status);

    // Convenience methods for UUID
    default List<Loan> findByUserId(UUID userId) {
        return findByUserId(userId.toString());
    }

    default Optional<Loan> findOneByIdAndBorrower(UUID id, User borrower) {
        return findOneByIdAndBorrower(id.toString(), borrower);
    }
}
