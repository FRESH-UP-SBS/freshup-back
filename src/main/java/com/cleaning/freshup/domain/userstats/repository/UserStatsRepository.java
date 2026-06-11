package com.cleaning.freshup.domain.userstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cleaning.freshup.domain.userstats.entity.UserStats;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

}
