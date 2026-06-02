package com.cleaning.freshup.domain.penalty.entity;

import com.cleaning.freshup.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이 클래스가 JPA Entity임을 의미한다.
// 즉, DB 테이블과 매핑되는 클래스이다.
@Entity

// Lombok 어노테이션
// 모든 필드의 getter 메서드를 자동으로 만들어준다.
// 예: getId(), getUser(), getAmount(), getAdjustmentYn()
@Getter

// Lombok 어노테이션
// 기본 생성자를 자동으로 만들어준다.
// JPA Entity는 기본 생성자가 필요하다.
@NoArgsConstructor

// 이 Entity가 DB의 TB_PENALTY 테이블과 연결된다는 뜻이다.
@Table(name = "TB_PENALTY")
public class Penalty {

    // 기본키(PK)를 의미한다.
    // TB_PENALTY 테이블의 PENALTY_SEQ 컬럼과 매핑된다.
    @Id

    // id 필드를 DB의 PENALTY_SEQ 컬럼과 연결한다.
    //
    // 주의:
    // 여기에는 @GeneratedValue가 없기 때문에
    // id 값이 자동 생성되도록 설정되어 있지 않다.
    // 즉, 이 Entity를 저장할 때는 id 값이 이미 정해져 있거나,
    // 다른 방식으로 관리되고 있을 가능성이 있다.
    @Column(name = "PENALTY_SEQ")
    private Long id;

    // 여러 Penalty는 하나의 User를 가질 수 있다.
    //
    // 예:
    // 한 명의 사용자가 여러 벌금 기록을 가질 수 있음
    //
    // fetch = FetchType.LAZY는 지연 로딩을 의미한다.
    // Penalty를 조회할 때 User 정보를 바로 가져오지 않고,
    // 실제로 user를 사용할 때 User 정보를 조회한다.
    @ManyToOne(fetch = FetchType.LAZY)

    // TB_PENALTY 테이블의 USER_SEQ 컬럼이
    // User 테이블과 연결되는 외래키(FK)라는 뜻이다.
    //
    // nullable = false는 USER_SEQ 값이 반드시 있어야 한다는 의미이다.
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    // 벌금 금액을 저장하는 필드이다.
    //
    // DB의 AMOUNT 컬럼과 매핑된다.
    //
    // nullable = false는
    // AMOUNT 값이 반드시 있어야 한다는 의미이다.
    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    // 벌금 정산 여부를 저장하는 필드이다.
    //
    // DB의 ADJUSTMENT_YN 컬럼과 매핑된다.
    //
    // 예:
    // "Y" → 정산 완료
    // "N" → 정산 필요
    //
    // nullable = false는
    // ADJUSTMENT_YN 값이 반드시 있어야 한다는 의미이다.
    @Column(name = "ADJUSTMENT_YN", nullable = false)
    private String adjustmentYn;

    // 벌금의 정산 여부 값을 수정하는 메서드이다.
    //
    // Entity의 필드를 직접 변경하지 않고,
    // 메서드를 통해 변경하도록 만든 구조이다.
    //
    // 예:
    // penalty.updateAdjustmentYn("Y");
    // → 해당 벌금을 정산 완료 상태로 변경
    public void updateAdjustmentYn(String adjustmentYn) {
        this.adjustmentYn = adjustmentYn;
    }
}