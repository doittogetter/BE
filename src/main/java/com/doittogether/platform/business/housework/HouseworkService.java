package com.doittogether.platform.business.housework;

import com.doittogether.platform.domain.entity.Housework;
import com.doittogether.platform.domain.entity.User;
import com.doittogether.platform.domain.enumeration.AssigneeStatus;
import com.doittogether.platform.presentation.dto.housework.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HouseworkService {
    HouseworkSliceResponse findAllByChannelIdAndTargetDate(final User loginUser, final Long channelId,
                                                           final LocalDate targetDate, final Pageable pageable);

    HouseworkSliceResponse findAllByChannelIdAndTargetDateAndAssigneeId(final User loginUser,
                                                                        final Long channelId,
                                                                        final LocalDate targetDate,
                                                                        final Long assigneeId,
                                                                        final Pageable pageable);

    HouseworkUserResponse assignHouseworkFromGPT(final HouseworkUserRequest request);

    void saveAssignee(final Long userId, final AssigneeStatus assigneeStatus);

    void addHousework(final User loginUser, final Long channelId, final HouseworkRequest request);

    void updateHousework(final User loginUser, final Long houseworkId, final Long channelId,
                                final HouseworkRequest request);

    void deleteHousework(final User loginUser, final Long houseworkId, final Long channelId);

    HouseworkResponse findHouseworkByChannelIdAndHouseworkId(final User user, final Long houseworkId, final Long channelId);

    void updateStatus(User loginUser, Long houseworkId, Long channelId);

    Map<String, Integer> calculateHouseworkStatisticsForWeek(Long channelId, LocalDate targetDate);

    List<Housework> weeklyHouseworkCheck(Long channelId, LocalDate targetDate);

    List<Housework> monthlyHouseworkCheck(Long channelId, LocalDate targetDate);

    IncompleteScoreResponse houseworkIncompleteCountCheck(User loginuser, Long channelId, LocalDate targetDate);
}
