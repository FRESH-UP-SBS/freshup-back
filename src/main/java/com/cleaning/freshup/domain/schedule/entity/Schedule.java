package com.cleaning.freshup.domain.schedule.entity;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.work.entity.Work;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 이 클래스가 JPA Entity임을 의미한다.
// 즉, DB 테이블과 매핑되는 클래스이다.
@Entity

// Lombok 어노테이션
// 모든 필드의 getter 메서드를 자동으로 만들어준다.
// 예: getId(), getUser(), getWork(), getDate()
@Getter

// Lombok 어노테이션
// 기본 생성자를 자동으로 만들어준다.
//
// access = AccessLevel.PROTECTED는
// 기본 생성자의 접근 범위를 protected로 만든다는 뜻이다.
//
// JPA Entity는 기본 생성자가 필요하지만,
// 외부에서 아무 값 없이 Schedule 객체를 마음대로 만들지 못하게 하기 위해
// public이 아니라 protected로 제한한 것이다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)

// 이 Entity가 DB의 TB_SCHEDULE 테이블과 연결된다는 뜻이다.
@Table(name = "TB_SCHEDULE")
public class Schedule {

    // 기본키(PK)를 의미한다.
    // TB_SCHEDULE 테이블의 EVENT_SEQ 컬럼과 매핑된다.
    @Id

    // 기본키 값을 자동으로 생성한다.
    // 여기서는 Oracle Sequence를 사용해서 id 값을 만든다.
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_seq")

    // 사용할 시퀀스 정보를 설정한다.
    //
    // name:
    // Java 코드에서 사용할 시퀀스 generator 이름
    //
    // sequenceName:
    // DB에 실제로 존재하는 시퀀스 이름
    //
    // allocationSize:
    // 시퀀스 값을 1씩 증가시킨다는 의미이다.
    @SequenceGenerator(
            name = "schedule_seq",
            sequenceName = "SEQ_SCHEDULE",
            allocationSize = 1
    )

    // id 필드를 DB의 EVENT_SEQ 컬럼과 연결한다.
    @Column(name = "EVENT_SEQ")
    private Long id;

    // 여러 Schedule은 하나의 User를 가질 수 있다.
    //
    // 예:
    // 한 명의 사용자가 여러 개의 청소 일정을 가질 수 있음
    //
    // fetch = FetchType.LAZY는 지연 로딩을 의미한다.
    // Schedule을 조회할 때 User 정보를 바로 가져오지 않고,
    // 실제로 user를 사용할 때 User 정보를 조회한다.
    @ManyToOne(fetch = FetchType.LAZY)

    // TB_SCHEDULE 테이블의 USER_SEQ 컬럼이
    // User 테이블과 연결되는 외래키(FK)라는 뜻이다.
    //
    // nullable = false는 USER_SEQ 값이 반드시 있어야 한다는 의미이다.
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    // 여러 Schedule은 하나의 Work를 가질 수 있다.
    //
    // 예:
    // 같은 청소 업무가 여러 일정에 등록될 수 있음
    //
    // fetch = FetchType.LAZY는 Work 정보도 실제로 사용할 때 조회한다는 의미이다.
    @ManyToOne(fetch = FetchType.LAZY)

    // TB_SCHEDULE 테이블의 WORK_SEQ 컬럼이
    // Work 테이블과 연결되는 외래키(FK)라는 뜻이다.
    //
    // nullable = false는 WORK_SEQ 값이 반드시 있어야 한다는 의미이다.
    @JoinColumn(name = "WORK_SEQ", nullable = false)
    private Work work;

    // 일정 날짜를 저장하는 필드이다.
    //
    // DB의 EVENT_DATE 컬럼과 매핑된다.
    //
    // LocalDate는 날짜만 표현하는 타입이다.
    // 시간 정보는 포함하지 않는다.
    //
    // 예:
    // 2026-05-07
    //
    // nullable = false는 EVENT_DATE 값이 반드시 있어야 한다는 의미이다.
    @Column(name = "EVENT_DATE", nullable = false)
    private LocalDate date;

    // Schedule 객체를 만들 때 사용하는 생성자이다.
    //
    // id는 직접 넣지 않는다.
    // id는 위에서 설정한 시퀀스를 통해 DB에 저장될 때 자동으로 생성된다.
    //
    // 예:
    // new Schedule(user, work, date)
    // → 특정 사용자(user)에게 특정 청소 업무(work)를 특정 날짜(date)에 배정
    public Schedule(User user, Work work, LocalDate date) {
        this.user = user;
        this.work = work;
        this.date = date;
    }

    // 기존 일정 정보를 수정하는 메서드이다.
    //
    // Entity의 필드를 외부에서 직접 바꾸는 대신,
    // update 메서드를 통해 한 번에 변경하는 구조이다.
    //
    // 수정되는 값:
    // - user: 일정 담당 사용자
    // - work: 청소 업무
    // - date: 일정 날짜
    //
    // 예:
    // schedule.update(newUser, newWork, newDate);
    // → 해당 일정의 담당자, 업무, 날짜를 수정
    public void update(User user, Work work, LocalDate date) {
        this.user = user;
        this.work = work;
        this.date = date;
    }
}