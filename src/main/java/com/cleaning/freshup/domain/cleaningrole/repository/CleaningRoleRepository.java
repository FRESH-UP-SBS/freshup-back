package com.cleaning.freshup.domain.cleaningrole.repository;

import com.cleaning.freshup.domain.cleaningrole.entity.CleaningRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CleaningRoleRepository extends JpaRepository<CleaningRole, Long> {

    List<CleaningRole> findByWorkId(Long workId);

    void deleteByWorkId(Long workId);
}