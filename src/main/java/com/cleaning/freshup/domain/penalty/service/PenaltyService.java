package com.cleaning.freshup.domain.penalty.service;

import com.cleaning.freshup.domain.penalty.dto.PenaltyResponseDto;
import com.cleaning.freshup.domain.penalty.entity.Penalty;
import com.cleaning.freshup.domain.penalty.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cleaning.freshup.domain.penalty.dto.PenaltyRequestDto;
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
// 여기서는 penaltyRepository를 주입받는 생성자가 자동 생성된다.
// 즉, new PenaltyRepository()를 직접 하지 않아도
// Spring이 알아서 객체를 넣어준다.
@RequiredArgsConstructor
public class PenaltyService {

    // 벌금 데이터를 DB에서 조회하거나 수정할 때 사용하는 Repository이다.
    //
    // Service는 직접 SQL을 실행하지 않고,
    // Repository를 통해 DB 작업을 요청한다.
    private final PenaltyRepository penaltyRepository;

    // 벌금 목록을 조회하는 메서드이다.
    //
    // Controller에서 GET /api/penalties 요청이 들어오면
    // 이 메서드가 호출된다.
    public List<PenaltyResponseDto> getPenalties() {

        // penaltyRepository.findAllWithUser()
        // → DB에서 모든 벌금 정보를 조회한다.
        // → 이때 벌금과 연결된 사용자 정보도 함께 조회한다.
        return penaltyRepository.findAllWithUser()

                // 조회된 List<Penalty>를 Stream으로 변환한다.
                //
                // Stream을 사용하면 리스트의 각 요소를
                // 하나씩 변환하거나 필터링할 수 있다.
                .stream()

                // Penalty Entity를 PenaltyResponseDto로 변환한다.
                //
                // PenaltyResponseDto::from 은 아래 코드와 같은 의미이다.
                // penalty -> PenaltyResponseDto.from(penalty)
                //
                // Entity를 그대로 클라이언트에게 보내지 않고,
                // 응답용 DTO로 바꿔서 보내기 위한 과정이다.
                .map(PenaltyResponseDto::from)

                // 변환된 PenaltyResponseDto들을 다시 List로 만든다.
                .toList();
    }

    // 벌금 정보를 수정하는 메서드이다.
    //
    // @Transactional은 이 메서드 안에서 일어나는 DB 작업을
    // 하나의 트랜잭션으로 묶어준다.
    //
    // 트랜잭션이란?
    // DB 작업을 하나의 단위로 처리하는 것이다.
    //
    // 이 메서드에서는 Penalty Entity의 값을 변경하기 때문에
    // @Transactional이 필요하다.
    @Transactional
    public PenaltyResponseDto updatePenalty(Long penaltyId, PenaltyRequestDto requestDto) {

        // penaltyId에 해당하는 벌금 정보를 DB에서 조회한다.
        //
        // findById(penaltyId)
        // → penaltyId와 같은 id를 가진 Penalty를 찾는다.
        //
        // orElseThrow(...)
        // → 해당 벌금 정보가 없으면 예외를 발생시킨다.
        //
        // 예:
        // penaltyId가 1이면 id가 1인 벌금 정보를 찾는다.
        // 없으면 "존재하지 않는 벌금 정보입니다." 오류가 발생한다.
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 벌금 정보입니다."));

        // 요청으로 받은 정산 여부 값을 Penalty Entity에 반영한다.
        //
        // requestDto.getAdjustmentYn()
        // → 클라이언트가 보낸 adjustmentYn 값을 가져온다.
        //
        // penalty.updateAdjustmentYn(...)
        // → Penalty Entity의 adjustmentYn 값을 수정한다.
        //
        // 예:
        // requestDto.getAdjustmentYn() 값이 "Y"이면
        // 해당 벌금은 정산 완료 상태로 변경된다.
        penalty.updateAdjustmentYn(requestDto.getAdjustmentYn());

        // 수정된 Penalty Entity를 응답용 DTO로 변환해서 반환한다.
        //
        // @Transactional 상태에서 Entity 값을 변경하면
        // JPA가 변경된 내용을 감지해서 DB에 반영한다.
        //
        // 즉, 여기서는 penaltyRepository.save(penalty)를 직접 호출하지 않아도
        // 트랜잭션이 끝날 때 수정 내용이 저장될 수 있다.
        return PenaltyResponseDto.from(penalty);
    }
}