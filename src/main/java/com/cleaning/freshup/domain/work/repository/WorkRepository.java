package com.cleaning.freshup.domain.work.repository;

import com.cleaning.freshup.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Work Entity를 DB에서 조회, 저장, 수정, 삭제할 때 사용하는 Repository이다.
//
// Repository란?
// DB와 직접 연결되는 계층이다.
// Service에서 Repository를 호출해서 DB 데이터를 가져오거나 저장한다.
//
// JpaRepository<Work, Long>의 의미:
// - Work: 이 Repository가 관리할 Entity 클래스
// - Long: Work Entity의 기본키(id) 타입
//
// JpaRepository를 상속하면 기본적인 DB 기능을 자동으로 사용할 수 있다.
//
// 예:
// - save()
//   → 저장 또는 수정
//
// - findById()
//   → id로 한 건 조회
//
// - findAll()
//   → 전체 조회
//
// - delete()
//   → 삭제
//
// - count()
//   → 개수 조회
public interface WorkRepository extends JpaRepository<Work, Long> {

    // useYn 값을 기준으로 Work 목록을 조회하고,
    // id 값을 기준으로 오름차순 정렬하는 메서드이다.
    //
    // Spring Data JPA는 메서드 이름을 보고 자동으로 쿼리를 만들어준다.
    //
    // 메서드 이름 해석:
    //
    // findByUseYn
    // → useYn 값이 매개변수 useYn과 같은 Work를 찾는다.
    //
    // OrderByIdAsc
    // → id 값을 기준으로 오름차순 정렬한다.
    //
    // 예:
    // findByUseYnOrderByIdAsc("Y")
    // → USE_YN 값이 "Y"인 청소 업무만 조회하고,
    //   WORK_SEQ 기준으로 작은 번호부터 정렬한다.
    //
    // 예:
    // findByUseYnOrderByIdAsc("N")
    // → USE_YN 값이 "N"인 청소 업무만 조회하고,
    //   WORK_SEQ 기준으로 작은 번호부터 정렬한다.
    List<Work> findByUseYnOrderByIdAsc(String useYn);
}