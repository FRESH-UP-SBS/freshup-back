package com.cleaning.freshup.domain.penalty.repository;

import com.cleaning.freshup.domain.penalty.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Penalty Entity를 DB에서 조회, 저장, 삭제할 때 사용하는 Repository이다.
//
// JpaRepository<Penalty, Long>의 의미:
// - Penalty: 이 Repository가 관리할 Entity 클래스
// - Long: Penalty Entity의 기본키(id) 타입
//
// JpaRepository를 상속하면 기본적인 DB 기능을 자동으로 사용할 수 있다.
// 예:
// - save()
// - findById()
// - findAll()
// - delete()
// - count()
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    // 모든 벌금 정보를 조회하되,
    // 벌금과 연결된 사용자 정보도 함께 조회하는 메서드이다.
    //
    // 일반적으로 @ManyToOne(fetch = FetchType.LAZY)로 설정된 user는
    // 실제로 사용할 때 따로 조회된다.
    //
    // 하지만 JOIN FETCH를 사용하면 Penalty를 조회할 때
    // User 정보까지 한 번에 같이 가져온다.
    //
    // 이렇게 하면 penalty.getUser().getName()처럼
    // 사용자 정보를 사용할 때 추가 쿼리가 반복해서 실행되는 것을 줄일 수 있다.
    @Query("""
            SELECT p
            FROM Penalty p
            JOIN FETCH p.user
            ORDER BY p.id ASC
            """)
    List<Penalty> findAllWithUser();

    // 특정 사용자의 전체 벌금 금액 합계를 구하는 메서드이다.
    //
    // SELECT COALESCE(SUM(p.amount), 0)
    // → 해당 사용자의 벌금 금액을 모두 더한다.
    // → 만약 더할 벌금 데이터가 없으면 null이 나올 수 있는데,
    //   COALESCE를 사용해서 null 대신 0을 반환한다.
    //
    // WHERE p.user.id = :userId
    // → Penalty와 연결된 User의 id가
    //   매개변수 userId와 같은 데이터만 대상으로 한다.
    //
    // 예:
    // sumAmountByUserId(3L)
    // → userId가 3번인 사용자의 전체 벌금 합계를 반환한다.
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Penalty p
            WHERE p.user.id = :userId
            """)
    int sumAmountByUserId(Long userId);

    // 특정 사용자의 미정산 벌금 금액 합계를 구하는 메서드이다.
    //
    // 전체 벌금 중에서 adjustmentYn 값이 'N'인 데이터만 합산한다.
    //
    // adjustmentYn = 'N'의 의미:
    // - 아직 정산되지 않은 벌금
    // - 정산이 필요한 벌금
    //
    // SELECT COALESCE(SUM(p.amount), 0)
    // → 조건에 맞는 벌금 금액을 모두 더한다.
    // → 조건에 맞는 데이터가 없으면 0을 반환한다.
    //
    // WHERE p.user.id = :userId
    // → 특정 사용자 데이터만 조회한다.
    //
    // AND p.adjustmentYn = 'N'
    // → 그중에서도 미정산 상태인 벌금만 조회한다.
    //
    // 예:
    // sumUnpaidAmountByUserId(3L)
    // → userId가 3번인 사용자의 미정산 벌금 합계를 반환한다.
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Penalty p
            WHERE p.user.id = :userId
            AND p.adjustmentYn = 'N'
            """)
    int sumUnpaidAmountByUserId(Long userId);
}