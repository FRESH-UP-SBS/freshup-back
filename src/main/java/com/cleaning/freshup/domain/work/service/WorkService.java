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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkService {

    private final WorkRepository workRepository;
    private final UserRepository userRepository;
    private final CleaningRoleRepository cleaningRoleRepository;

    public List<WorkResponseDto> getWorks() {
        return workRepository.findByUseYnOrderByIdAsc("Y")
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public WorkResponseDto createWork(WorkRequestDto requestDto) {
        Work work = new Work(requestDto.getWorkName());
        Work savedWork = workRepository.save(work);

        saveCleaningRoles(savedWork, requestDto.getMemberIds());

        return toResponseDto(savedWork);
    }

    @Transactional
    public WorkResponseDto updateWork(Long workId, WorkRequestDto requestDto) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 업무입니다."));

        work.updateWorkName(requestDto.getWorkName());

        // 기존 담당자 먼저 DB에서 삭제
        cleaningRoleRepository.deleteByWorkId(workId);

        // delete SQL이 먼저 DB에 반영되도록 강제 flush
        cleaningRoleRepository.flush();

        // 새 담당자 다시 저장
        saveCleaningRoles(work, requestDto.getMemberIds());

        return toResponseDto(work);
    }

    @Transactional
    public void deleteWork(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 업무입니다."));

        cleaningRoleRepository.deleteByWorkId(workId);
        cleaningRoleRepository.flush();

        work.deleteWork();
    }

    private void saveCleaningRoles(Work work, List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }

        List<Long> distinctMemberIds = memberIds.stream()
                .distinct()
                .toList();

        for (Long memberId : distinctMemberIds) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

            CleaningRole cleaningRole = new CleaningRole(user, work);

            cleaningRoleRepository.save(cleaningRole);
        }
    }

    private WorkResponseDto toResponseDto(Work work) {
        List<CleaningRole> cleaningRoles = cleaningRoleRepository.findByWorkId(work.getId());

        List<Long> memberIds = cleaningRoles.stream()
                .map(cleaningRole -> cleaningRole.getUser().getId())
                .toList();

        List<String> memberNames = cleaningRoles.stream()
                .map(cleaningRole -> cleaningRole.getUser().getName())
                .toList();

        return WorkResponseDto.builder()
                .id(work.getId())
                .workName(work.getWorkName())
                .memberIds(memberIds)
                .memberNames(memberNames)
                .build();
    }
}