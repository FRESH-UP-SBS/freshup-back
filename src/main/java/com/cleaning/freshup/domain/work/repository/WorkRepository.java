package com.cleaning.freshup.domain.work.repository;

import com.cleaning.freshup.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}