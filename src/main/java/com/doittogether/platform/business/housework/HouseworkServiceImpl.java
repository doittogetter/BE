package com.doittogether.platform.business.housework;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.housework.HouseworkException;
import com.doittogether.platform.application.global.exception.statistics.StatisticsException;
import com.doittogether.platform.business.channel.ChannelValidator;
import com.doittogether.platform.business.openai.AssignChoreChatGPTService;
import com.doittogether.platform.business.openai.dto.AssignChoreChatGPTResponse;
import com.doittogether.platform.domain.entity.Assignee;
import com.doittogether.platform.domain.entity.Channel;
import com.doittogether.platform.domain.entity.Housework;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.enumeration.HouseworkCategory;
import com.doittogether.platform.domain.enumeration.Status;
import com.doittogether.platform.infrastructure.persistence.housework.AssigneeRepository;
import com.doittogether.platform.infrastructure.persistence.housework.HouseworkRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.housework.HouseworkRequest;
import com.doittogether.platform.presentation.dto.housework.HouseworkResponse;
import com.doittogether.platform.presentation.dto.housework.HouseworkSliceResponse;
import com.doittogether.platform.presentation.dto.housework.HouseworkUserRequest;
import com.doittogether.platform.presentation.dto.housework.HouseworkUserResponse;
import com.doittogether.platform.presentation.dto.housework.IncompleteScoreResponse;
import com.doittogether.platform.presentation.dto.housework.PersonalIncompleteScoreResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.doittogether.platform.domain.entity.Assignee.assignAssignee;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class HouseworkServiceImpl implements HouseworkService {

    private final EntityManager entityManager;
    private final HouseworkRepository houseworkRepository;
    private final AssigneeRepository assigneeRepository;
    private final HouseworkValidator houseworkValidator;
    private final ChannelValidator channelValidator;
    private final UserRepository userRepository;
    private final AssignChoreChatGPTService assignChoreChatGPTService;

    @Override
    public HouseworkUserResponse assignHouseworkFromGPT(final HouseworkUserRequest request) {
        Long userId = 0L;
        String housework = null;

        try {
            AssignChoreChatGPTResponse assignChoreChatGPTResponse = assignChoreChatGPTService.chat(request);
            String jsonResponse = assignChoreChatGPTResponse.getChoices().get(0).getMessage().getContent();


            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);

            userId = Long.valueOf(responseMap.get("UserId").toString());
            housework = responseMap.get("Housework").toString();
        } catch (Exception e) {
            log.error("Unable to assign chore using AssignChoreChatGPTService. Exception: {}",
                    e.getMessage(), e);
        }

        if (userId == 0L || housework == null) {
            log.error("집안일 담당자가 정상적으로 담기지 않았습니다.");
            throw new HouseworkException(ExceptionCode._INTERNAL_SERVER_ERROR);
        }

        saveAssignee(userId, request);
        return HouseworkUserResponse.of(userId, housework);
    }

    @Override
    public void saveAssignee(Long userId,
                             final HouseworkUserRequest request) {
        Housework housework = houseworkRepository.findByChannelChannelIdAndHouseworkId(
                request.channelId(),
                request.houseworkId()
        ).orElseThrow(() -> new RuntimeException("Housework not found for channelId: " + request.channelId()
                + ", houseworkId: " + request.houseworkId()));

        User newAssignee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for userId: " + userId));

        housework.updateAssignee(assignAssignee(newAssignee));

        houseworkRepository.save(housework);
    }

    @Override
    @Transactional(readOnly = true)
    public HouseworkSliceResponse findAllByChannelIdAndTargetDate(final User loginUser,
                                                                  final Long channelId,
                                                                  final LocalDate targetDate,
                                                                  final Pageable pageable) {
        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        final Slice<Housework> houseworks = houseworkRepository.findAllByChannelIdAndTargetDate(
                channelId, loginUser.retrieveUserId(), pageable, targetDate);

        return HouseworkSliceResponse.from(houseworks);
    }

    @Override
    @Transactional(readOnly = true)
    public HouseworkSliceResponse findAllByChannelIdAndTargetDateAndAssigneeId(final User loginUser,
                                                                               final Long channelId,
                                                                               final LocalDate targetDate,
                                                                               final Long assigneeId,
                                                                               final Pageable pageable) {
        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        final Slice<Housework> houseworks = houseworkRepository.findAllByChannelIdAndTargetDateAndAssigneeId(channelId,
                assigneeId, pageable, targetDate);
        return HouseworkSliceResponse.from(houseworks);
    }

    @Override
    public HouseworkResponse findHouseworkByChannelIdAndHouseworkId(final User loginUser,
                                                                    final Long channelId,
                                                                    final Long houseworkId) {
        channelValidator.validateExistChannel(channelId);
        houseworkValidator.validateExistHousework(houseworkId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        Housework housework = houseworkRepository.findByChannelChannelIdAndHouseworkId(channelId, houseworkId)
                .orElseThrow(() -> new HouseworkException(ExceptionCode.HOUSEWORK_NOT_FOUND));

        return HouseworkResponse.from(housework);
    }

    @Override
    public void addHousework(final User loginUser, final Long channelId, final HouseworkRequest request) {
        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        User houseworkAssignee = userRepository.findById(request.userId())
                .orElseThrow(() -> new HouseworkException(ExceptionCode.USER_NOT_FOUND));
        channelValidator.checkChannelInAssignee(houseworkAssignee, channelId);
        final Channel channel = entityManager.getReference(Channel.class, channelId);

        try {
            final Assignee assignee = assigneeRepository.findByUserUserId(request.userId())
                    .orElseGet(() -> assignAssignee(userRepository.findById(request.userId())
                            .orElseThrow(() -> new HouseworkException(ExceptionCode.USER_NOT_FOUND))));
            final Assignee saveAssignee = assigneeRepository.saveAndFlush(assignee);
            final Housework housework = Housework.of(
                    request.startDate(),
                    request.startTime(),
                    request.task(),
                    HouseworkCategory.parse(request.category()),
                    saveAssignee,
                    channel);
            houseworkRepository.save(housework);
        } catch (IllegalArgumentException exception) {
            throw new HouseworkException(ExceptionCode.HOUSEWORK_NOT_NULL);
        }
    }

    @Override
    public void updateHousework(final User loginUser, final Long houseworkId, final Long channelId,
                                final HouseworkRequest request) {
        channelValidator.validateExistChannel(channelId);
        houseworkValidator.validateExistHousework(houseworkId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        final Housework housework = entityManager.getReference(Housework.class, houseworkId);
        try {
            final Assignee assignee = assigneeRepository.findByUserUserId(request.userId())
                    .orElseGet(() -> assignAssignee(userRepository.findById(request.userId())
                            .orElseThrow(() -> new HouseworkException(ExceptionCode.USER_NOT_FOUND))));
            final Housework updateHousework = housework.update(request, assignee);
            houseworkRepository.save(updateHousework);
        } catch (IllegalArgumentException exception) {
            throw new HouseworkException(ExceptionCode.HOUSEWORK_NOT_NULL);
        }
    }

    @Override
    public void updateStatus(User loginUser, Long channelId, Long houseworkId) {
        channelValidator.validateExistChannel(channelId);
        houseworkValidator.validateExistHousework(houseworkId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        final Housework housework = houseworkRepository.findByChannelChannelIdAndHouseworkId(channelId, houseworkId)
                .orElseThrow(() -> new HouseworkException(ExceptionCode.HOUSEWORK_NOT_NULL));
        houseworkValidator.validateEditableUser(housework, loginUser);
        housework.updateStatus();
        houseworkRepository.save(housework);
    }

    @Override
    public Map<String, Integer> calculateHouseworkStatisticsForWeek(Long channelId, LocalDate targetDate) {
        LocalDate startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)); // 일 부터
        LocalDate endOfWeek = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)); // 토 까지

        int completedCount = houseworkRepository.countByStatusAndDateRange(
                channelId, Status.COMPLETE, startOfWeek, endOfWeek);

        int uncompletedCount = houseworkRepository.countByStatusAndDateRange(
                channelId, Status.UN_COMPLETE, startOfWeek, endOfWeek);

        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("completeCount", completedCount);
        statistics.put("unCompletedCount", uncompletedCount);
        return statistics;
    }

    @Override
    public void deleteHousework(final User loginUser, final Long houseworkId, final Long channelId) {
        channelValidator.validateExistChannel(channelId);
        houseworkValidator.validateExistHousework(houseworkId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        final Housework housework = houseworkRepository.findById(houseworkId).orElseThrow();
        try {
            houseworkRepository.delete(housework);
        } catch (IllegalArgumentException exception) {
            throw new HouseworkException(ExceptionCode.HOUSEWORK_NOT_NULL);
        }
    }

    @Override
    public IncompleteScoreResponse incompleteScoreResponse(User loginUser, Long channelId, LocalDate targetDate) {
        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);

        final LocalDate startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        final LocalDate endOfWeek = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        List<Housework> houseworkList = houseworkRepository.findByChannelChannelIdAndStartDateBetween(channelId, startOfWeek, endOfWeek);
        try {
            List<PersonalIncompleteScoreResponse> HouseworkList = houseworkDailyIncompleteCountCheck(houseworkList, startOfWeek, endOfWeek);
            return IncompleteScoreResponse.of(HouseworkList);
        } catch (IllegalArgumentException e) {
            throw new StatisticsException(ExceptionCode.HOUSEWORK_NOT_NULL);
        }
    }

    public List<PersonalIncompleteScoreResponse> houseworkDailyIncompleteCountCheck(
            List<Housework> houseworkList, LocalDate startDate, LocalDate endDate) {

        // 날짜별 집안일 그룹화
        Map<LocalDate, List<Housework>> groupedByDate = houseworkList.stream()
                .collect(Collectors.groupingBy(Housework::retrieveStartDate));

        // 날짜 범위 내 모든 날짜 초기화
        List<PersonalIncompleteScoreResponse> houseworkCheckList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<Housework> dailyHouseworks = groupedByDate.getOrDefault(currentDate, Collections.emptyList());

            // 전체 집안일 개수
            int totalTasks = dailyHouseworks.size();

            // 미진행 집안일 개수
            int incompletedTasks = (int) dailyHouseworks.stream()
                    .filter(housework -> housework.retrieveStatus() == Status.UN_COMPLETE)
                    .count();

            // 상태 계산
            boolean status = calculateCompletionStatus(totalTasks, incompletedTasks);

            // 응답 객체 생성
            houseworkCheckList.add(new PersonalIncompleteScoreResponse(currentDate, incompletedTasks, status));

            currentDate = currentDate.plusDays(1); // 다음 날짜로 이동
        }

        // 날짜 오름차순 정렬 (이미 정렬된 경우 생략 가능)
        houseworkCheckList.sort(Comparator.comparing(PersonalIncompleteScoreResponse::retrieveDate));
        return houseworkCheckList;
    }

    private boolean calculateCompletionStatus(int totalTasks, int incompletedTasks) {
        return totalTasks != 0 && incompletedTasks == 0;
    }
}
