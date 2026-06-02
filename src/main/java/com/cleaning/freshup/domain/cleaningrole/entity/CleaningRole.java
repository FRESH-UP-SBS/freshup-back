package com.cleaning.freshup.domain.cleaningrole.entity;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이 클래스가 JPA Entity임을 의미한다.
// 즉, DB 테이블과 매핑되는 클래스이다.
@Entity

// Lombok 어노테이션
// 모든 필드의 getter 메서드를 자동으로 만들어준다.
// 예: getId(), getUser(), getWork()
@Getter

// Lombok 어노테이션
// 기본 생성자를 자동으로 만들어준다.
// JPA Entity는 기본 생성자가 반드시 필요하다.
@NoArgsConstructor

// 이 Entity가 DB의 TB_CLEANING_ROLE 테이블과 연결된다는 뜻이다.
@Table(name = "TB_CLEANING_ROLE")
public class CleaningRole {

    // 기본키(PK)를 의미한다.
    // TB_CLEANING_ROLE 테이블의 CLEANING_ROLE_SEQ 컬럼과 매핑된다.
    @Id

    // 기본키 값을 자동으로 생성한다.
    // 여기서는 Oracle Sequence를 사용해서 id 값을 만든다.
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cleaning_role_seq")

    // 사용할 시퀀스 정보를 설정한다.
    // name: Java 코드에서 사용할 시퀀스 generator 이름
    // sequenceName: DB에 실제로 존재하는 시퀀스 이름
    // allocationSize: 시퀀스 값을 1씩 증가시킨다는 의미
    @SequenceGenerator(
            name = "cleaning_role_seq",
            sequenceName = "SEQ_CLEANING_ROLE",
            allocationSize = 1
    )

    // id 필드를 DB의 CLEANING_ROLE_SEQ 컬럼과 연결한다.
    @Column(name = "CLEANING_ROLE_SEQ")
    private Long id;

    // 여러 CleaningRole은 하나의 User를 가질 수 있다.
    // 예: 한 명의 사용자가 여러 청소 역할을 맡을 수 있음
    //
    // fetch = FetchType.LAZY는 지연 로딩을 의미한다.
    // CleaningRole을 조회할 때 User 정보를 바로 가져오지 않고,
    // 실제로 user를 사용할 때 가져온다.
    @ManyToOne(fetch = FetchType.LAZY)

    // TB_CLEANING_ROLE 테이블의 USER_SEQ 컬럼이
    // User 테이블과 연결되는 외래키(FK)라는 뜻이다.
    //
    // nullable = false는 USER_SEQ 값이 반드시 있어야 한다는 의미이다.
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    // 여러 CleaningRole은 하나의 Work를 가질 수 있다.
    // 예: 같은 청소 업무가 여러 사용자에게 배정될 수 있음
    //
    // fetch = FetchType.LAZY는 Work 정보도 실제로 사용할 때 조회한다는 의미이다.
    @ManyToOne(fetch = FetchType.LAZY)

    // TB_CLEANING_ROLE 테이블의 WORK_SEQ 컬럼이
    // Work 테이블과 연결되는 외래키(FK)라는 뜻이다.
    //
    // nullable = false는 WORK_SEQ 값이 반드시 있어야 한다는 의미이다.
    @JoinColumn(name = "WORK_SEQ", nullable = false)
    private Work work;

    // CleaningRole 객체를 만들 때 사용할 생성자이다.
    //
    // id는 직접 넣지 않는다.
    // id는 위에서 설정한 시퀀스를 통해 DB에 저장될 때 자동으로 생성된다.
    //
    // 예:
    // new CleaningRole(user, work)
    // → 특정 사용자(user)에게 특정 청소 업무(work)를 배정하는 객체 생성
    public CleaningRole(User user, Work work) {
        this.user = user;
        this.work = work;
    }
}