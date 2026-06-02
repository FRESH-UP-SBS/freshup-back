package com.cleaning.freshup.domain.schedule.dto;

import com.cleaning.freshup.domain.schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// 일정 정보를 클라이언트에게 응답할 때 사용하는 DTO이다.
//
// Entity를 그대로 응답하지 않고 DTO로 변환해서 응답하면,
// 화면에 필요한 데이터만 골라서 보낼 수 있다.
//
// 예:
// Schedule Entity 안에는 User, Work 같은 객체가 들어있지만,
// 화면에서는 사용자 이름, 업무 이름 정도만 필요할 수 있다.
// 그래서 이 DTO에서 필요한 값만 꺼내서 응답한다.
@Getter

// Lombok 어노테이션
// Builder 패턴을 사용할 수 있게 해준다.
//
// Builder 패턴을 사용하면 객체를 만들 때
// 어떤 필드에 어떤 값을 넣는지 보기 쉬워진다.
//
// 예:
// ScheduleResponseDto.builder()
//      .id(1L)
//      .date(LocalDate.now())
//      .taskName("화장실 청소")
//      .memberName("홍길동")
//      .build();
@Builder
public class ScheduleResponseDto {

    // 일정의 고유 번호이다.
    // Schedule Entity의 id 값이 들어간다.
    private Long id;

    // 일정 날짜이다.
    //
    // LocalDate는 날짜만 표현하는 타입이다.
    // 시간 정보는 포함하지 않는다.
    //
    // 예:
    // 2026-05-07
    private LocalDate date;

    // 청소 업무 이름이다.
    //
    // Schedule과 연결된 Work Entity의 workName 값이 들어간다.
    //
    // 예:
    // "설거지", "화장실 청소", "바닥 청소"
    private String taskName;

    // 일정에 배정된 사용자 이름이다.
    //
    // Schedule과 연결된 User Entity의 name 값이 들어간다.
    private String memberName;

    // Schedule Entity를 ScheduleResponseDto로 변환하는 정적 메서드이다.
    //
    // static 메서드이기 때문에 객체를 만들지 않고도 사용할 수 있다.
    //
    // 예:
    // ScheduleResponseDto dto = ScheduleResponseDto.from(schedule);
    public static ScheduleResponseDto from(Schedule schedule) {

        // Builder 패턴을 사용해서 ScheduleResponseDto 객체를 생성한다.
        return ScheduleResponseDto.builder()

                // 일정 고유 번호를 DTO에 넣는다.
                .id(schedule.getId())

                // 일정 날짜를 DTO에 넣는다.
                .date(schedule.getDate())

                // Schedule과 연결된 Work에서 업무 이름을 꺼내 DTO에 넣는다.
                //
                // schedule.getWork()
                // → 이 일정에 연결된 청소 업무 객체를 가져온다.
                //
                // getWorkName()
                // → 그 청소 업무의 이름을 가져온다.
                .taskName(schedule.getWork().getWorkName())

                // Schedule과 연결된 User에서 사용자 이름을 꺼내 DTO에 넣는다.
                //
                // schedule.getUser()
                // → 이 일정에 배정된 사용자 객체를 가져온다.
                //
                // getName()
                // → 그 사용자의 이름을 가져온다.
                .memberName(schedule.getUser().getName())

                // 위에서 설정한 값들로 ScheduleResponseDto 객체를 최종 생성한다.
                .build();
    }
}