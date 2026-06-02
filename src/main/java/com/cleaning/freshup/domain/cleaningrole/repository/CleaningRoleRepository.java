package com.cleaning.freshup.domain.cleaningrole.repository;

import com.cleaning.freshup.domain.cleaningrole.entity.CleaningRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// CleaningRole Entity를 DB에서 조회, 저장, 삭제할 때 사용하는 Repository이다.
//
// JpaRepository<CleaningRole, Long>의 의미:
// - CleaningRole: 이 Repository가 관리할 Entity 클래스
// - Long: CleaningRole Entity의 기본키(id) 타입
//
// JpaRepository를 상속하면 기본적인 DB 기능을 자동으로 사용할 수 있다.
// 예:
// - save()
// - findById()
// - findAll()
// - delete()
// - count()
public interface CleaningRoleRepository extends JpaRepository<CleaningRole, Long> {

    // workId를 기준으로 CleaningRole 목록을 조회한다.
    //
    // 메서드 이름 findByWorkId는 Spring Data JPA가 자동으로 해석한다.
    //
    // 의미:
    // CleaningRole 안에 있는 work 필드의 id 값이
    // 매개변수 workId와 같은 데이터를 조회한다.
    //
    // 예:
    // findByWorkId(3L)
    // → WORK_SEQ가 3번인 청소 역할 목록을 가져온다.
    List<CleaningRole> findByWorkId(Long workId);

    // 이 메서드는 SELECT가 아니라 DELETE 쿼리를 실행한다.
    //
    // @Modifying은 JPQL로 데이터를 수정하거나 삭제할 때 필요하다.
    // 즉, insert/update/delete 쿼리에는 @Modifying을 붙여야 한다.
    //
    // flushAutomatically = true는
    // 삭제 쿼리를 실행하기 전에 JPA가 가지고 있던 변경 내용을
    // DB에 먼저 반영하도록 하는 옵션이다.
    @Modifying(flushAutomatically = true)

    // JPQL 삭제 쿼리이다.
    //
    // delete from CleaningRole cr
    // → CleaningRole Entity에서 데이터를 삭제한다.
    //
    // where cr.work.id = :workId
    // → CleaningRole이 가지고 있는 work의 id 값이
    //    매개변수 workId와 같은 데이터만 삭제한다.
    //
    // 주의:
    // 여기서 CleaningRole은 DB 테이블명이 아니라 Entity 클래스명이다.
    // cr.work.id는 CleaningRole Entity 안의 work 필드 안에 있는 id를 의미한다.
    @Query("delete from CleaningRole cr where cr.work.id = :workId")

    // 특정 workId에 연결된 CleaningRole 데이터를 모두 삭제한다.
    //
    // 예:
    // deleteByWorkId(3L)
    // → WORK_SEQ가 3번인 청소 역할 배정 데이터를 모두 삭제한다.
    void deleteByWorkId(Long workId);
}