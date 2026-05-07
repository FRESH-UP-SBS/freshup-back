package com.cleaning.freshup.domain.work.dto;

import com.cleaning.freshup.domain.work.entity.Work;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// 청소 업무 정보를 클라이언트에게 응답할 때 사용하는 DTO이다.
//
// Entity를 그대로 응답하지 않고 DTO로 변환해서 응답하면,
// 화면에 필요한 데이터만 골라서 보낼 수 있다.
//
// 예:
// Work Entity에는 청소 업무 자체의 정보가 들어 있고,
// 이 DTO에는 화면에서 사용할 업무 id, 업무명, 담당자 id 목록,
// 담당자 이름 목록 등을 담을 수 있다.
@Getter

// Lombok 어노테이션
// Builder 패턴을 사용할 수 있게 해준다.
//
// Builder 패턴을 사용하면 객체를 만들 때
// 어떤 필드에 어떤 값을 넣는지 보기 쉬워진다.
//
// 예:
// WorkResponseDto.builder()
//      .id(1L)
//      .workName("화장실 청소")
//      .build();
@Builder
public class WorkResponseDto {

    // 청소 업무의 고유 번호이다.
    //
    // Work Entity의 id 값이 들어간다.
    private Long id;

    // 청소 업무 이름이다.
    //
    // 예:
    // "설거지"
    // "화장실 청소"
    // "바닥 청소"
    private String workName;

    // 이 청소 업무를 담당하는 사용자들의 고유 번호 목록이다.
    //
    // List<Long>의 의미:
    // - List: 여러 개의 값을 담을 수 있는 목록
    // - Long: 사용자 id 값의 타입
    //
    // 예:
    // memberIds = [1, 2, 3]
    // → 1번, 2번, 3번 사용자가 이 업무 담당자라는 의미이다.
    private List<Long> memberIds;

    // 이 청소 업무를 담당하는 사용자들의 이름 목록이다.
    //
    // 예:
    // memberNames = ["홍길동", "김철수"]
    // → 홍길동, 김철수가 이 업무 담당자라는 의미이다.
    private List<String> memberNames;

    // Work Entity를 WorkResponseDto로 변환하는 정적 메서드이다.
    //
    // static 메서드이기 때문에 객체를 만들지 않고도 사용할 수 있다.
    //
    // 예:
    // WorkResponseDto dto = WorkResponseDto.from(work);
    public static WorkResponseDto from(Work work) {

        // Builder 패턴을 사용해서 WorkResponseDto 객체를 생성한다.
        return WorkResponseDto.builder()

                // 청소 업무 고유 번호를 DTO에 넣는다.
                .id(work.getId())

                // 청소 업무 이름을 DTO에 넣는다.
                .workName(work.getWorkName())

                // 위에서 설정한 값들로 WorkResponseDto 객체를 최종 생성한다.
                //
                // 현재 from() 메서드에서는 id와 workName만 넣고 있다.
                // memberIds, memberNames는 여기서 따로 설정하지 않으므로 null 상태가 된다.
                .build();
    }
}