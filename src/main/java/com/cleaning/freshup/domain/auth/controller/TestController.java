package com.cleaning.freshup.domain.auth.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/test")
    public String test() {
        return "백엔드 연결 성공";
    }

    @GetMapping("/api/db-test")
    public String dbTest() {
        String result = jdbcTemplate.queryForObject(
                "SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM dual",
                String.class
        );

        return "DB 연결 성공: " + result;
    }
}