package com.cleaning.freshup.domain.penalty.repository;

import com.cleaning.freshup.domain.penalty.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    @Query("""
            SELECT p
            FROM Penalty p
            JOIN FETCH p.user
            ORDER BY p.id ASC
            """)
    List<Penalty> findAllWithUser();
}