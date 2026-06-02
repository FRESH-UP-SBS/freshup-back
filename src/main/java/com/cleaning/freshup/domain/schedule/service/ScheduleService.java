package com.cleaning.freshup.domain.schedule.service;

import com.cleaning.freshup.domain.schedule.dto.ScheduleRequestDto;
import com.cleaning.freshup.domain.schedule.dto.ScheduleResponseDto;
import com.cleaning.freshup.domain.schedule.entity.Schedule;
import com.cleaning.freshup.domain.schedule.repository.ScheduleRepository;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.work.entity.Work;
import com.cleaning.freshup.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// 이 클래스가 Service 역할을 하는 클래스라는 뜻이다.
//
// Service는 Controller와 Repository 사이에서
// 실제 비즈니스 로직을 처리하는 계층이다.
//
// 예:
// Controller → 요청 받기
// Service → 실제 로직 처리
// Repository → DB 접근
@Service

// Lombok 어노테이션
// final이 붙은 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 아래의 scheduleRepository, userRepository, workRepository를
// 주입받는 생성자가 자동 생성된다.
//
// 즉, Repository 객체를 직접 new 하지 않아도
// Spring이 알아서 넣어준다.
@RequiredArgsConstructor

// 이 클래스의 모든 메서드에 기본적으로 읽기 전용 트랜잭션을 적용한다.
//
// readOnly = true는
// DB 데이터를 조회만 할 때 사용하는 설정이다.
//
// 조회 성능에 도움이 될 수 있고,
// 실수로 데이터를 변경하는 일을 줄일 수 있다.
//
// 단, 등록/수정/삭제 메서드는 아래에서 따로 @Transactional을 붙여
// readOnly가 아닌 일반 트랜잭션으로 동작하게 한다.
@Transactional(readOnly = true)
public class ScheduleService {

    // 일정 데이터를 DB에서 조회, 저장, 수정, 삭제할 때 사용하는 Repository이다.
    private final ScheduleRepository scheduleRepository;

    // 사용자 데이터를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 일정 등록/수정 시 userId로 실제 User Entity를 찾기 위해 사용한다.
    private final UserRepository userRepository;

    // 청소 업무 데이터를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 일정 등록/수정 시 workId로 실제 Work Entity를 찾기 위해 사용한다.
    private final WorkRepository workRepository;

    // 특정 연도와 월에 해당하는 일정 목록을 조회하는 메서드이다.
    //
    // 예:
    // getSchedules(2026, 5)
    // → 2026년 5월 일정 목록을 조회한다.
    public List<ScheduleResponseDto> getSchedules(int year, int month) {

        // 조회할 월의 시작 날짜를 만든다.
        //
        // LocalDate.of(year, month, 1)
        // → 해당 연도, 해당 월의 1일 날짜를 만든다.
        //
        // 예:
        // year = 2026, month = 5
        // → 2026-05-01
        LocalDate start = LocalDate.of(year, month, 1);

        // 조회할 월의 마지막 날짜를 만든다.
        //
        // start.lengthOfMonth()
        // → 해당 월이 며칠까지 있는지 구한다.
        //
        // start.withDayOfMonth(...)
        // → start 날짜의 day 값을 해당 월의 마지막 일자로 바꾼다.
        //
        // 예:
        // start = 2026-05-01
        // start.lengthOfMonth() = 31
        // end = 2026-05-31
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Repository에게 start부터 end 사이의 일정 목록 조회를 요청한다.
        return scheduleRepository.findSchedulesByMonth(start, end)

                // 조회된 List<Schedule>을 Stream으로 변환한다.
                //
                // Stream을 사용하면 리스트 안의 데이터를
                // 하나씩 변환하거나 필터링할 수 있다.
                .stream()

                // Schedule Entity를 ScheduleResponseDto로 변환한다.
                //
                // ScheduleResponseDto::from은 아래 코드와 같은 의미이다.
                // schedule -> ScheduleResponseDto.from(schedule)
                //
                // Entity를 그대로 클라이언트에게 보내지 않고,
                // 화면에 필요한 응답용 DTO로 바꾸는 과정이다.
                .map(ScheduleResponseDto::from)

                // 변환된 ScheduleResponseDto들을 다시 List로 만든다.
                .toList();
    }

    // 새로운 일정을 등록하는 메서드이다.
    //
    // @Transactional은 이 메서드 안에서 일어나는 DB 작업을
    // 하나의 트랜잭션으로 묶어준다.
    //
    // 이 메서드는 DB에 데이터를 저장하므로
    // readOnly = true가 아닌 일반 @Transactional이 필요하다.
    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {

        // 요청으로 받은 userId를 이용해서 User Entity를 조회한다.
        //
        // requestDto.getUserId()
        // → 클라이언트가 보낸 사용자 고유 번호를 가져온다.
        //
        // findById(...)
        // → 해당 id를 가진 사용자를 DB에서 찾는다.
        //
        // orElseThrow(...)
        // → 해당 사용자가 없으면 예외를 발생시킨다.
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 요청으로 받은 workId를 이용해서 Work Entity를 조회한다.
        //
        // requestDto.getWorkId()
        // → 클라이언트가 보낸 청소 업무 고유 번호를 가져온다.
        //
        // 해당 업무가 없으면 예외를 발생시킨다.
        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업무입니다."));

        // 조회한 user, work와 요청으로 받은 date를 이용해서
        // 새로운 Schedule Entity 객체를 생성한다.
        //
        // 이 시점에는 아직 DB에 저장된 상태가 아니다.
        Schedule schedule = new Schedule(
                user,
                work,
                requestDto.getDate()
        );

        // 새로 만든 Schedule Entity를 DB에 저장한다.
        //
        // save(schedule)
        // → INSERT 쿼리가 실행되어 일정이 등록된다.
        //
        // 저장된 Entity를 savedSchedule 변수에 담는다.
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 저장된 Schedule Entity를 응답용 DTO로 변환해서 반환한다.
        //
        // Controller는 이 값을 클라이언트에게 JSON 형태로 응답한다.
        return ScheduleResponseDto.from(savedSchedule);
    }

    // 기존 일정을 수정하는 메서드이다.
    //
    // 이 메서드는 DB 데이터를 변경하므로 @Transactional이 필요하다.
    @Transactional
    public ScheduleResponseDto updateSchedule(Long scheduleId, ScheduleRequestDto requestDto) {

        // scheduleId에 해당하는 일정 정보를 DB에서 조회한다.
        //
        // findById(scheduleId)
        // → 해당 id를 가진 Schedule을 찾는다.
        //
        // 없으면 "존재하지 않는 일정입니다." 예외를 발생시킨다.
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        // 요청으로 받은 userId를 이용해서 User Entity를 조회한다.
        //
        // 일정 담당자를 수정할 때
        // 실제 User Entity가 필요하기 때문에 조회한다.
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 요청으로 받은 workId를 이용해서 Work Entity를 조회한다.
        //
        // 일정에 연결할 청소 업무를 수정할 때
        // 실제 Work Entity가 필요하기 때문에 조회한다.
        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업무입니다."));

        // 조회한 기존 Schedule Entity의 값을 수정한다.
        //
        // 수정되는 값:
        // - user: 담당 사용자
        // - work: 청소 업무
        // - date: 일정 날짜
        //
        // @Transactional 안에서 Entity 값을 변경하면
        // JPA가 변경 내용을 감지해서 트랜잭션이 끝날 때 DB에 반영한다.
        //
        // 그래서 여기서는 save(schedule)를 직접 호출하지 않아도
        // 수정 내용이 저장될 수 있다.
        schedule.update(
                user,
                work,
                requestDto.getDate()
        );

        // 수정된 Schedule Entity를 응답용 DTO로 변환해서 반환한다.
        return ScheduleResponseDto.from(schedule);
    }

    // 기존 일정을 삭제하는 메서드이다.
    //
    // 이 메서드는 DB 데이터를 삭제하므로 @Transactional이 필요하다.
    @Transactional
    public void deleteSchedule(Long scheduleId) {

        // scheduleId에 해당하는 일정 정보를 DB에서 조회한다.
        //
        // 삭제하려면 먼저 어떤 Schedule을 삭제할지 찾아야 한다.
        //
        // 해당 일정이 없으면 예외를 발생시킨다.
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        // 조회한 Schedule Entity를 DB에서 삭제한다.
        //
        // delete(schedule)
        // → DELETE 쿼리가 실행되어 해당 일정이 삭제된다.
        scheduleRepository.delete(schedule);
    }
}