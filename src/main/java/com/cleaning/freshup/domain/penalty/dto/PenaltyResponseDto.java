package com.cleaning.freshup.domain.penalty.dto;

import com.cleaning.freshup.domain.penalty.entity.Penalty;
import lombok.Builder;
import lombok.Getter;

// 벌금 정보를 클라이언트에게 응답할 때 사용하는 DTO이다.
//
// Entity를 그대로 응답하지 않고 DTO로 변환해서 응답하면,
// 필요한 데이터만 골라서 내려줄 수 있고,
// Entity 구조가 외부에 직접 노출되는 것을 줄일 수 있다.
@Getter

// Lombok 어노테이션
// Builder 패턴을 사용할 수 있게 해준다.
//
// 예:
// PenaltyResponseDto.builder()
//      .id(1L)
//      .name("홍길동")
//      .build();
@Builder
public class PenaltyResponseDto {

    // 벌금 데이터의 고유 번호이다.
    // Penalty Entity의 id 값이 들어간다.
    private Long id;

    // 벌금을 낸 사용자 또는 벌금 대상 사용자의 고유 번호이다.
    // Penalty와 연결된 User Entity의 id 값이 들어간다.
    private Long userId;

    // 벌금 대상 사용자의 이름이다.
    // Penalty와 연결된 User Entity의 name 값이 들어간다.
    private String name;

    // 벌금 금액이다.
    // 예: 5000, 10000
    private Integer amount;

    // 정산 여부를 나타내는 값이다.
    //
    // 예:
    // "Y" → 정산 완료
    // "N" → 정산 필요
    private String adjustmentYn;

    // 화면에 보여줄 정산 상태 문구이다.
    //
    // adjustmentYn 값에 따라
    // "정산완료" 또는 "정산필요"가 들어간다.
    private String status;

    // Penalty Entity를 PenaltyResponseDto로 변환하는 정적 메서드이다.
    //
    // static 메서드이기 때문에 객체를 만들지 않고도 사용할 수 있다.
    //
    // 예:
    // PenaltyResponseDto dto = PenaltyResponseDto.from(penalty);
    public static PenaltyResponseDto from(Penalty penalty) {

        // Penalty Entity에서 정산 여부 값을 꺼낸다.
        //
        // 이 값을 아래에서 adjustmentYn 필드와 status 필드에 사용한다.
        String adjustmentYn = penalty.getAdjustmentYn();

        // Builder 패턴을 사용해서 PenaltyResponseDto 객체를 생성한다.
        return PenaltyResponseDto.builder()

                // 벌금 고유 번호를 DTO에 넣는다.
                .id(penalty.getId())

                // Penalty와 연결된 User의 고유 번호를 DTO에 넣는다.
                //
                // penalty.getUser()는 벌금과 연결된 사용자 객체를 의미하고,
                // getId()는 그 사용자의 id 값을 가져온다.
                .userId(penalty.getUser().getId())

                // Penalty와 연결된 User의 이름을 DTO에 넣는다.
                .name(penalty.getUser().getName())

                // 벌금 금액을 DTO에 넣는다.
                .amount(penalty.getAmount())

                // 정산 여부 값을 DTO에 넣는다.
                .adjustmentYn(adjustmentYn)

                // adjustmentYn 값이 "Y"이면 "정산완료",
                // 그렇지 않으면 "정산필요"를 DTO에 넣는다.
                //
                // "Y".equals(adjustmentYn) 방식은
                // adjustmentYn이 null이어도 오류가 나지 않게 하기 위한 안전한 비교 방식이다.
                .status("Y".equals(adjustmentYn) ? "정산완료" : "정산필요")

                // 위에서 설정한 값들로 PenaltyResponseDto 객체를 최종 생성한다.
                .build();
    }
}