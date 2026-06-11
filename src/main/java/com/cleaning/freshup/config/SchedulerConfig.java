package com.cleaning.freshup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Scheduling 활성화 설정
 * 
 * @Scheduled 어노테이션이 동작하려면 반드시 필요합니다.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // 필요 시 ThreadPoolTaskScheduler 빈을 등록해 스케줄러 스레드 수를 조정할 수 있습니다.
    //
    // @Bean
    // public TaskScheduler taskScheduler() {
    // ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    // scheduler.setPoolSize(2);
    // scheduler.setThreadNamePrefix("penalty-scheduler-");
    // return scheduler;
    // }
}
