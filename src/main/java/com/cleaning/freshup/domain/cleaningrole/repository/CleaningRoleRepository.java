package com.cleaning.freshup.domain.cleaningrole.repository;

import com.cleaning.freshup.domain.cleaningrole.entity.CleaningRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CleaningRoleRepository extends JpaRepository<CleaningRole, Long> {

    List<CleaningRole> findByWorkId(Long workId);

    @Modifying(flushAutomatically = true)
    @Query("delete from CleaningRole cr where cr.work.id = :workId")
    void deleteByWorkId(Long workId);
}