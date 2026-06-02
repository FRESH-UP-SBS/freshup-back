package com.cleaning.freshup.domain.work.service;

import com.cleaning.freshup.domain.cleaningrole.entity.CleaningRole;
import com.cleaning.freshup.domain.cleaningrole.repository.CleaningRoleRepository;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.work.dto.WorkRequestDto;
import com.cleaning.freshup.domain.work.dto.WorkResponseDto;
import com.cleaning.freshup.domain.work.entity.Work;
import com.cleaning.freshup.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
// 여기서는 아래의 workRepository, userRepository, cleaningRoleRepository를
// 주입받는 생성자가 자동 생성된다.
//
// 즉, Repository 객체를 직접 new 하지 않아도
// Spring이 알아서 넣어준다.
@RequiredArgsConstructor

// 이 클래스의 모든 메서드에 기본적으로 읽기 전용 트랜잭션을 적용한다.
//
// readOnly = true는 DB 데이터를 조회만 할 때 사용하는 설정이다.
//
// 단, 등록/수정/삭제 메서드는 아래에서 따로 @Transactional을 붙여
// readOnly가 아닌 일반 트랜잭션으로 동작하게 한다.
@Transactional(readOnly = true)
public class WorkService {

    // 청소 업무 데이터를 DB에서 조회, 저장, 수정할 때 사용하는 Repository이다.
    private final WorkRepository workRepository;

    // 사용자 데이터를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 청소 업무 담당자를 저장할 때
    // memberId로 실제 User Entity를 찾기 위해 사용한다.
    private final UserRepository userRepository;

    // 청소 업무와 담당자의 연결 정보를 DB에서 조회, 저장, 삭제할 때 사용하는 Repository이다.
    //
    // CleaningRole은
    // "어떤 사용자가 어떤 청소 업무를 담당하는지"를 나타내는 Entity이다.
    private final CleaningRoleRepository cleaningRoleRepository;

    // 사용 중인 청소 업무 목록을 조회하는 메서드이다.
    //
    // Controller에서 GET /api/works 요청이 들어오면
    // 이 메서드가 호출된다.
    public List<WorkResponseDto> getWorks() {

        // USE_YN 값이 "Y"인 청소 업무만 조회한다.
        //
        // "Y"는 사용 중인 업무를 의미한다.
        // id 기준 오름차순으로 정렬해서 가져온다.
        return workRepository.findByUseYnOrderByIdAsc("Y")

                // 조회된 List<Work>를 Stream으로 변환한다.
                //
                // Stream을 사용하면 리스트 안의 데이터를
                // 하나씩 변환하거나 필터링할 수 있다.
                .stream()

                // Work Entity를 WorkResponseDto로 변환한다.
                //
                // this::toResponseDto는 아래 코드와 같은 의미이다.
                // work -> this.toResponseDto(work)
                //
                // 여기서 담당자 id 목록과 담당자 이름 목록까지 함께 담아준다.
                .map(this::toResponseDto)

                // 변환된 WorkResponseDto들을 다시 List로 만든다.
                .toList();
    }

    // 새로운 청소 업무를 등록하는 메서드이다.
    //
    // 이 메서드는 DB에 데이터를 저장하므로 @Transactional이 필요하다.
    @Transactional
    public WorkResponseDto createWork(WorkRequestDto requestDto) {

        // 요청으로 받은 청소 업무 이름을 이용해서
        // 새로운 Work Entity를 만든다.
        //
        // requestDto.getWorkName()
        // → 클라이언트가 보낸 청소 업무 이름을 가져온다.
        //
        // 예:
        // "화장실 청소"
        Work work = new Work(requestDto.getWorkName());

        // 새로 만든 Work Entity를 DB에 저장한다.
        //
        // save(work)
        // → INSERT 쿼리가 실행되어 청소 업무가 등록된다.
        //
        // 저장된 Work Entity를 savedWork 변수에 담는다.
        Work savedWork = workRepository.save(work);

        // 새로 등록한 청소 업무에 담당자들을 연결해서 저장한다.
        //
        // savedWork:
        // 방금 저장한 청소 업무
        //
        // requestDto.getMemberIds():
        // 클라이언트가 보낸 담당자 id 목록
        //
        // 예:
        // memberIds = [1, 2]
        // → 1번, 2번 사용자를 이 업무 담당자로 저장한다.
        saveCleaningRoles(savedWork, requestDto.getMemberIds());

        // 저장된 Work Entity를 응답용 DTO로 변환해서 반환한다.
        return toResponseDto(savedWork);
    }

    // 기존 청소 업무를 수정하는 메서드이다.
    //
    // 이 메서드는 DB 데이터를 변경하므로 @Transactional이 필요하다.
    @Transactional
    public WorkResponseDto updateWork(Long workId, WorkRequestDto requestDto) {

        // workId에 해당하는 청소 업무를 DB에서 조회한다.
        //
        // findById(workId)
        // → workId와 같은 id를 가진 Work를 찾는다.
        //
        // orElseThrow(...)
        // → 해당 청소 업무가 없으면 예외를 발생시킨다.
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 업무입니다."));

        // 청소 업무 이름을 수정한다.
        //
        // requestDto.getWorkName()
        // → 클라이언트가 보낸 새 청소 업무 이름을 가져온다.
        //
        // work.updateWorkName(...)
        // → Work Entity의 workName 값을 변경한다.
        work.updateWorkName(requestDto.getWorkName());

        // 기존 담당자 연결 정보를 먼저 DB에서 삭제한다.
        //
        // 예:
        // 기존에 1번, 2번 사용자가 담당자였다면
        // 해당 업무와 연결된 CleaningRole 데이터를 모두 삭제한다.
        cleaningRoleRepository.deleteByWorkId(workId);

        // delete SQL이 먼저 DB에 반영되도록 강제로 flush한다.
        //
        // flush()는 JPA가 가지고 있던 변경 내용을
        // 즉시 DB에 반영하도록 하는 메서드이다.
        //
        // 여기서는 기존 담당자 삭제가 먼저 확실히 반영된 뒤,
        // 새 담당자를 다시 저장하기 위해 사용한다.
        cleaningRoleRepository.flush();

        // 요청으로 받은 새 담당자 목록을 다시 저장한다.
        //
        // 즉, 기존 담당자를 모두 지우고
        // 새 담당자 목록으로 교체하는 방식이다.
        saveCleaningRoles(work, requestDto.getMemberIds());

        // 수정된 Work Entity를 응답용 DTO로 변환해서 반환한다.
        return toResponseDto(work);
    }

    // 청소 업무를 삭제 처리하는 메서드이다.
    //
    // 이 메서드는 DB 데이터를 변경하므로 @Transactional이 필요하다.
    @Transactional
    public void deleteWork(Long workId) {

        // workId에 해당하는 청소 업무를 DB에서 조회한다.
        //
        // 삭제 처리하려면 먼저 어떤 Work를 처리할지 찾아야 한다.
        //
        // 해당 청소 업무가 없으면 예외를 발생시킨다.
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 업무입니다."));

        // 해당 청소 업무와 연결된 담당자 정보를 삭제한다.
        //
        // 즉, 이 업무를 맡고 있던 사용자 연결 정보들을 먼저 제거한다.
        cleaningRoleRepository.deleteByWorkId(workId);

        // 담당자 연결 정보 삭제가 DB에 바로 반영되도록 강제한다.
        cleaningRoleRepository.flush();

        // 청소 업무를 삭제 처리한다.
        //
        // 실제 Work 데이터를 DB에서 완전히 삭제하는 것이 아니라,
        // Work Entity 안의 useYn 값을 "N"으로 변경한다.
        //
        // 이런 방식을 소프트 삭제라고 한다.
        work.deleteWork();
    }

    // 청소 업무와 담당자들을 연결해서 저장하는 private 메서드이다.
    //
    // private 메서드는 이 클래스 내부에서만 사용할 수 있다.
    //
    // 이 메서드는 createWork(), updateWork()에서 공통으로 사용된다.
    private void saveCleaningRoles(Work work, List<Long> memberIds) {

        // 담당자 목록이 없으면 저장할 담당자가 없으므로 바로 종료한다.
        //
        // memberIds == null
        // → 담당자 목록 자체가 없는 경우
        //
        // memberIds.isEmpty()
        // → 담당자 목록은 있지만 비어 있는 경우
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        // 담당자 id 목록에서 중복을 제거한다.
        //
        // distinct()
        // → 같은 값이 여러 번 들어온 경우 하나만 남긴다.
        //
        // 예:
        // [1, 1, 2, 3]
        // → [1, 2, 3]
        List<Long> distinctMemberIds = memberIds.stream()
                .distinct()
                .toList();

        // 중복 제거된 담당자 id 목록을 하나씩 반복한다.
        for (Long memberId : distinctMemberIds) {

            // memberId에 해당하는 User Entity를 DB에서 조회한다.
            //
            // findById(memberId)
            // → memberId와 같은 id를 가진 User를 찾는다.
            //
            // 해당 사용자가 없으면 예외를 발생시킨다.
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            // 조회한 user와 전달받은 work를 연결하는 CleaningRole 객체를 만든다.
            //
            // 의미:
            // 이 사용자가 이 청소 업무의 담당자라는 연결 데이터 생성
            CleaningRole cleaningRole = new CleaningRole(user, work);

            // CleaningRole Entity를 DB에 저장한다.
            //
            // 즉, 담당자 배정 정보가 저장된다.
            cleaningRoleRepository.save(cleaningRole);
        }
    }

    // Work Entity를 WorkResponseDto로 변환하는 private 메서드이다.
    //
    // Entity를 그대로 클라이언트에게 보내지 않고,
    // 화면에서 필요한 형태의 DTO로 바꿔서 반환하기 위해 사용한다.
    private WorkResponseDto toResponseDto(Work work) {

        // 현재 Work에 연결된 담당자 목록을 조회한다.
        //
        // work.getId()
        // → 현재 청소 업무의 고유 번호를 가져온다.
        //
        // findByWorkId(...)
        // → 해당 업무를 담당하는 CleaningRole 목록을 조회한다.
        List<CleaningRole> cleaningRoles = cleaningRoleRepository.findByWorkId(work.getId());

        // CleaningRole 목록에서 담당자 User의 id만 꺼내서 List<Long>으로 만든다.
        //
        // 예:
        // CleaningRole 안에 User 1번, User 2번이 있으면
        // memberIds = [1, 2]
        List<Long> memberIds = cleaningRoles.stream()
                .map(cleaningRole -> cleaningRole.getUser().getId())
                .toList();

        // CleaningRole 목록에서 담당자 User의 이름만 꺼내서 List<String>으로 만든다.
        //
        // 예:
        // CleaningRole 안에 홍길동, 김철수가 있으면
        // memberNames = ["홍길동", "김철수"]
        List<String> memberNames = cleaningRoles.stream()
                .map(cleaningRole -> cleaningRole.getUser().getName())
                .toList();

        // Work Entity와 담당자 정보를 WorkResponseDto에 담아서 반환한다.
        return WorkResponseDto.builder()

                // 청소 업무 고유 번호를 DTO에 넣는다.
                .id(work.getId())

                // 청소 업무 이름을 DTO에 넣는다.
                .workName(work.getWorkName())

                // 담당자 id 목록을 DTO에 넣는다.
                .memberIds(memberIds)

                // 담당자 이름 목록을 DTO에 넣는다.
                .memberNames(memberNames)

                // 위에서 설정한 값들로 WorkResponseDto 객체를 최종 생성한다.
                .build();
    }
}