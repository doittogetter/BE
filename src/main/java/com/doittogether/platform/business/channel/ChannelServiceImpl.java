package com.doittogether.platform.business.channel;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.channel.ChannelException;
import com.doittogether.platform.business.invite.InviteLinkService;
import com.doittogether.platform.business.preset.PresetService;
import com.doittogether.platform.domain.entity.*;
import com.doittogether.platform.domain.enumeration.Role;
import com.doittogether.platform.infrastructure.persistence.channel.UserChannelRepository;
import com.doittogether.platform.infrastructure.persistence.housework.AssigneeRepository;
import com.doittogether.platform.infrastructure.persistence.housework.HouseworkRepository;
import com.doittogether.platform.infrastructure.persistence.reaction.ReactionRepository;
import com.doittogether.platform.infrastructure.persistence.user.UserRepository;
import com.doittogether.platform.infrastructure.persistence.channel.ChannelRepository;
import com.doittogether.platform.presentation.dto.channel.request.ChannelInviteLinkTestRequest;
import com.doittogether.platform.presentation.dto.channel.request.ChannelKickUserRequest;
import com.doittogether.platform.presentation.dto.channel.request.ChannelRegisterRequest;
import com.doittogether.platform.presentation.dto.channel.request.ChannelUpdateRequest;
import com.doittogether.platform.presentation.dto.channel.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final UserRepository userRepository;
    private final UserChannelRepository userChannelRepository;
    private final ChannelRepository channelRepository;
    private final ReactionRepository reactionRepository;
    private final AssigneeRepository assigneeRepository;

    private final InviteLinkService inviteLinkService;
    private final PresetService presetService;
    private final HouseworkRepository houseworkRepository;

    @Override
    public ChannelListResponse getMyChannels(User loginUser, Pageable pageable) {
        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));

        Pageable resolvedPageable = resolveSort(pageable);
        Page<UserChannel> userChannels = userChannelRepository.findByUser(user, resolvedPageable);

        Page<ChannelResponse> channelResponses = userChannels.map(ChannelResponse::from);

        return ChannelListResponse.of(user, channelResponses);
    }

    @Override
    @Transactional
    public ChannelRegisterResponse createChannel(User loginUser, ChannelRegisterRequest request) {
        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));

        Channel channel = ChannelRegisterRequest.toEntity(request);
        channel = channelRepository.save(channel);

        UserChannel userChannel = UserChannel.of(user, channel, Role.ADMIN);
        userChannelRepository.save(userChannel);

        presetService.addDefaultCategoriesToChannel(channel);

        return ChannelRegisterResponse.of(channel.getChannelId(), channel.getName());
    }

    @Override
    public ChannelUpdateResponse updateChannelName(User loginUser, Long channelId, ChannelUpdateRequest request) {
        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        UserChannel userChannel = userChannelRepository.findByUserAndChannel(user, channel)
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_IN_CHANNEL));

        if (!userChannel.isRoleAdmin()) {
            throw new ChannelException(ExceptionCode.CHANNEL_ACCESS_DENIED);
        }

        channel.updateName(request.name());
        Channel updatedChannel = channelRepository.save(channel);

        return new ChannelUpdateResponse(
                updatedChannel.getChannelId()
        );
    }

    @Override
    public ChannelUserListResponse getChannelUsers(User loginUser, Long channelId, Pageable pageable) {
        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        userChannelRepository.findByUserAndChannel(user, channel)
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_IN_CHANNEL));

        Pageable resolvedPageable = resolveSort(pageable);
        Page<UserChannel> userChannels = userChannelRepository.findByChannel(channel, resolvedPageable);

        Page<UserChannelResponse> userChannelResponses = userChannels.map(userChannel ->
                UserChannelResponse.from(userChannel, userChannel.getUser().equals(user))
        );

        return ChannelUserListResponse.of(channel, userChannelResponses);
    }

    @Override
    public ChannelInviteLinkResponse generateInviteLink(Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        String inviteLink = inviteLinkService.generateInviteLink(channelId);

        return ChannelInviteLinkResponse.of(channel, inviteLink);
    }

    @Override
    public ChannelInviteLinkResponse generateInviteLinkTest(Long channelId, ChannelInviteLinkTestRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        String inviteLink = inviteLinkService.generateInviteLinkTest(channelId, request);

        return ChannelInviteLinkResponse.of(channel, inviteLink);
    }

    @Override
    public ChannelJoinResponse joinChannelViaInviteLink(User loginUser, String inviteLink) {
        Long channelId = inviteLinkService.validateInviteLink(inviteLink);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));

        boolean isUserInChannel = userChannelRepository.existsByUserAndChannel(user, channel);
        if (isUserInChannel) {
            throw new ChannelException(ExceptionCode.USER_ALREADY_IN_CHANNEL);
        }

        UserChannel userChannel = UserChannel.of(user, channel, Role.PARTICIPANT);
        userChannelRepository.save(userChannel);

        return ChannelJoinResponse.of(channel);
    }

    @Transactional
    @Override
    public ChannelKickUserResponse kickUserFromChannel(User loginUser, Long channelId, ChannelKickUserRequest request) {
        User adminUser = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        UserChannel adminUserChannel = userChannelRepository.findByUserAndChannel(adminUser, channel)
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_IN_CHANNEL));

        if (!adminUserChannel.isRoleAdmin()) {
            throw new ChannelException(ExceptionCode.CHANNEL_ACCESS_DENIED);
        }

        User targetUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ChannelException(ExceptionCode.ASSIGNEE_NOT_IN_CHANNEL));

        // 추방 당하는 사용자의 알림들 연관 삭제
        reactionRepository.deleteByUserIdOrTargetUserId(targetUser.getUserId());

        // 추방 당하는 사용자의 집안일 연관 삭제
        assigneeRepository.findByUserUserId(targetUser.getUserId())
                .ifPresent(assignee -> houseworkRepository.deleteByAssigneeId(assignee.retrieveAssigneeId()));

        UserChannel targetUserChannel = userChannelRepository.findByUserAndChannel(targetUser, channel)
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_IN_CHANNEL));

        userChannelRepository.delete(targetUserChannel);

        return ChannelKickUserResponse.from(targetUser);
    }

    @Override
    @Transactional
    public void leaveChannels(User loginUser, Long... channelIds) {
        List<Long> channelIdList = Arrays.asList(channelIds);
        leaveChannels(loginUser, channelIdList);
    }

    @Override
    @Transactional
    public void leaveChannels(User loginUser, List<Long> channelIds) {
        for (Long channelId : channelIds) {
            leaveChannel(loginUser, channelId);
        }
    }

    private void leaveChannel(User loginUser, Long channelId) {
        User user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_NOT_FOUND));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelException(ExceptionCode.CHANNEL_NOT_FOUND));

        UserChannel userChannel = userChannelRepository.findByUserAndChannel(user, channel)
                .orElseThrow(() -> new ChannelException(ExceptionCode.USER_CHANNEL_RELATION_NOT_FOUND));

        // 나가는 사용자의 알림들 연관 삭제
        reactionRepository.deleteByUserIdOrTargetUserId(user.getUserId());

        // 나가는 사용자의 집안일 연관 삭제
        assigneeRepository.findByUserUserId(user.getUserId())
                .ifPresent(assignee -> houseworkRepository.deleteByAssigneeId(assignee.retrieveAssigneeId()));

        if (userChannel.isRoleAdmin()) { // 관리자 이라면,
            if (channel.getUserChannels().size() == 1) { // 방에 관리자가 혼자 남은 경우
                channelRepository.delete(channel);
                return;
            }

            UserChannel newAdmin = userChannelRepository.findFirstByChannelAndRoleNot(channel, Role.ADMIN)
                    .orElseThrow(() -> new ChannelException(ExceptionCode.UNABLE_TO_ASSIGN_NEW_ADMIN));
            newAdmin.assignNewAdmin(); // 다른 사용자에게 관리자 권한 부여
        }

        userChannelRepository.delete(userChannel);
    }

    private Pageable resolveSort(Pageable pageable) {
        Map<String, String> fieldMapping = Map.of(
                "userId", "user.userId",
                "nickName", "user.nickName",
                "email", "user.email",

                "channelId", "channel.channelId",
                "name", "channel.name"
        );

        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> {
                    String mappedProperty = fieldMapping.getOrDefault(order.getProperty(), order.getProperty());
                    return new Sort.Order(order.getDirection(), mappedProperty);
                })
                .toList();

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }
}
