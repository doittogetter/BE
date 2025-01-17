package com.doittogether.platform.business.stastics;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.statistics.StatisticsException;
import com.doittogether.platform.business.channel.ChannelValidator;
import com.doittogether.platform.business.housework.HouseworkService;
import com.doittogether.platform.business.reaction.ReactionService;
import com.doittogether.platform.domain.entity.Assignee;
import com.doittogether.platform.domain.entity.Channel;
import com.doittogether.platform.domain.entity.Housework;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.enumeration.CompletionStatus;
import com.doittogether.platform.domain.enumeration.Status;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.presentation.dto.stastics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final ChannelValidator channelValidator;
    private final HouseworkService houseworkService;
    private final ReactionService reactionService;

    @Override
    public CompleteScoreResponse calculateWeeklyStatistics(User loginUser, Long channelId, LocalDate targetDate) {

        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);

        final List<Housework> houseworkList = houseworkService.monthlyHouseworkCheck(channelId, targetDate);

        try {
            final List<PersonalCompleteScoreResponse> statisticsList = generateWeeklyStatistics(houseworkList);

            return CompleteScoreResponse.of(statisticsList);
        } catch (IllegalArgumentException e) {
            throw new StatisticsException(ExceptionCode.HOUSEWORK_NOT_NULL);
        }
    }

    @Override
    public ChannelCountStatisticsResponse calculateTotalCountByChannelId(User loginUser, Long channelId,
                                                                         LocalDate targetDate) {
        Channel channel = channelValidator.validateAndGetChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);

        Map<String, Integer> houseworkStatistics = houseworkService.calculateHouseworkStatisticsForWeek(channelId, targetDate);
        Map<String, Integer> reactionStatistics = reactionService.calculateReactionStatisticsForWeek(channelId, targetDate);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("channelName", channel.getName());
        statistics.put("completeCount", houseworkStatistics.getOrDefault("completeCount", 0));
        statistics.put("unCompletedCount", houseworkStatistics.getOrDefault("unCompletedCount", 0));
        statistics.put("complimentCount", reactionStatistics.getOrDefault("complimentCount", 0));
        statistics.put("pokeCount", reactionStatistics.getOrDefault("pokeCount", 0));

        return ChannelCountStatisticsResponse.of(statistics);
    }

    @Override
    public MonthlyStatisticsResponse calculateMonthlyStatistics(User loginUser, Long channelId, LocalDate targetDate) {
        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);

        final List<Housework> houseworkList = houseworkService.monthlyHouseworkCheck(channelId, targetDate);
        try {
            List<SingleDayStatisticsResponse> statisticsList = generateMonthlyStatistics(houseworkList);

            return MonthlyStatisticsResponse.of(statisticsList);
        } catch (IllegalArgumentException e) {
            throw new StatisticsException(ExceptionCode.HOUSEWORK_NOT_NULL);
        }
    }

    @Override
    public MonthlyMVPResponse calculateMonthlyMVP(User loginUser, Long channelId, LocalDate targetDate) {
        channelValidator.validateExistChannel(channelId);
        channelValidator.checkChannelParticipation(loginUser, channelId);
        Map<String, Object> reactionStatistics = reactionService.calculateReactionsStatisticsMVPForMonthly(channelId, targetDate);

        return MonthlyMVPResponse.of(reactionStatistics);
    }

    public List<PersonalCompleteScoreResponse> generateWeeklyStatistics(List<Housework> houseworkList) {
        return houseworkList.stream()
                .collect(Collectors.groupingBy(Housework::getAssignee)) // Assignee별 그룹화
                .entrySet().stream()
                .map(entry -> {
                    Assignee assignee = entry.getKey();
                    List<Housework> dailyHouseworks = entry.getValue();

                    String nickName = assignee.retrieveUser().getNickName();
                    long completedTasks = dailyHouseworks.stream()
                            .filter(housework -> housework.getStatus() == Status.COMPLETE)
                            .count();
                    String profileImageUrl = userRepository.findProfileImageUrlByNickName(nickName).orElse("");

                    return new PersonalCompleteScoreResponse(nickName, Math.toIntExact(completedTasks), profileImageUrl);
                })
                .sorted(Comparator.comparing(PersonalCompleteScoreResponse::completeCount).reversed()) // 완료 개수 내림차순 정렬
                .toList();
    }

    public List<SingleDayStatisticsResponse> generateMonthlyStatistics(List<Housework> houseworkList) {
        return houseworkList.stream()
                .collect(Collectors.groupingBy(Housework::getStartDate)) // 날짜별 그룹화
                .entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Housework> dailyHouseworks = entry.getValue();

                    int totalTasks = dailyHouseworks.size();
                    long completedTasks = dailyHouseworks.stream()
                            .filter(housework -> housework.geteStatus() == Status.COMPLETE)
                            .count();
                    CompletionStatus status = calculateCompletionStatus(dailyHouseworks, totalTasks);

                    return new SingleDayStatisticsResponse(date, totalTasks, Math.toIntExact(completedTasks), status);
                })
                .sorted(Comparator.comparing(SingleDayStatisticsResponse::retrieveDate)) // 날짜 기준 오름차순 정렬
                .toList();
    }

    private CompletionStatus calculateCompletionStatus(List<Housework> houseworks, int totalTasks) {
        if (totalTasks == 0) {
            return CompletionStatus.NO_HOUSEWORK;
        }

        if (houseworks.stream().allMatch(h -> h.getStatus() == Status.COMPLETE)) {
            return CompletionStatus.ALL_DONE;
        }

        return CompletionStatus.INCOMPLETE_REMAINING;
    }


}
