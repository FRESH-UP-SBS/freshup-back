package com.cleaning.freshup.domain.work.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이 클래스가 JPA Entity임을 의미한다.
// 즉, DB 테이블과 매핑되는 클래스이다.
@Entity

// Lombok 어노테이션
// 모든 필드의 getter 메서드를 자동으로 만들어준다.
// 예: getId(), getWorkName(), getUseYn()
@Getter

// Lombok 어노테이션
// 기본 생성자를 자동으로 만들어준다.
// JPA Entity는 기본 생성자가 반드시 필요하다.
@NoArgsConstructor

// 이 Entity가 DB의 TB_WORK 테이블과 연결된다는 뜻이다.
@Table(name = "TB_WORK")
public class Work {

    // 기본키(PK)를 의미한다.
    // TB_WORK 테이블의 WORK_SEQ 컬럼과 매핑된다.
    @Id

    // 기본키 값을 자동으로 생성한다.
    // 여기서는 Oracle Sequence를 사용해서 id 값을 만든다.
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_seq")

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
            name = "work_seq",
            sequenceName = "SEQ_WORK",
            allocationSize = 1
    )

    // id 필드를 DB의 WORK_SEQ 컬럼과 연결한다.
    @Column(name = "WORK_SEQ")
    private Long id;

    // 청소 업무 이름을 저장하는 필드이다.
    //
    // DB의 WORK_NAME 컬럼과 매핑된다.
    //
    // 예:
    // "설거지"
    // "화장실 청소"
    // "바닥 청소"
    //
    // nullable = false는
    // WORK_NAME 값이 반드시 있어야 한다는 의미이다.
    @Column(name = "WORK_NAME", nullable = false)
    private String workName;

    // 이 청소 업무를 사용할지 여부를 저장하는 필드이다.
    //
    // DB의 USE_YN 컬럼과 매핑된다.
    //
    // 예:
    // "Y" → 사용 중
    // "N" → 사용하지 않음
    //
    // 기본값이 "Y"이므로,
    // Work 객체가 만들어질 때 별도로 값을 넣지 않으면
    // 사용 중 상태로 설정된다.
    @Column(name = "USE_YN")
    private String useYn = "Y";

    // Work 객체를 만들 때 사용하는 생성자이다.
    //
    // id는 직접 넣지 않는다.
    // id는 위에서 설정한 시퀀스를 통해 DB에 저장될 때 자동으로 생성된다.
    //
    // 예:
    // new Work("화장실 청소")
    // → "화장실 청소"라는 청소 업무 객체를 생성
    public Work(String workName) {
        this.workName = workName;

        // 새로 생성되는 청소 업무는 기본적으로 사용 중 상태로 만든다.
        //
        // "Y" → 사용 중
        this.useYn = "Y";
    }

    // 청소 업무 이름을 수정하는 메서드이다.
    //
    // Entity의 필드를 외부에서 직접 바꾸는 대신,
    // 메서드를 통해 변경하도록 만든 구조이다.
    //
    // 예:
    // work.updateWorkName("분리수거")
    // → 청소 업무 이름을 "분리수거"로 변경
    public void updateWorkName(String workName) {
        this.workName = workName;
    }

    // 청소 업무를 삭제 처리하는 메서드이다.
    //
    // 실제 DB 데이터 자체를 바로 삭제하는 것이 아니라,
    // useYn 값을 "N"으로 바꿔서 사용하지 않는 상태로 만든다.
    //
    // 이런 방식을 보통 소프트 삭제라고 한다.
    //
    // 예:
    // work.deleteWork()
    // → USE_YN 값이 "N"으로 변경되어 화면이나 목록에서 제외할 수 있음
    public void deleteWork() {
        this.useYn = "N";
    }
}