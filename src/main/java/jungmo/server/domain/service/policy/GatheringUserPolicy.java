package jungmo.server.domain.service.policy;

import jungmo.server.domain.entity.Authority;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GatheringUserPolicy {

    private final GatheringUserRepository gatheringUserRepository;

    public void validateAuthority(User writeUser, Gathering gathering, Authority authority) {
        gatheringUserRepository.findByAuthority(writeUser, gathering, authority)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_AUTHORITY));
    }

    public void validateUsers(List<User> usersToUpdate, List<Long> userIds) {
        if (usersToUpdate.size() != userIds.size()) {
            throw new BusinessException(ErrorCode.USER_NOT_EXISTS);
        }
    }

    public void validateNewUsers(Long currentUserId, Set<Long> foundUserIds, Set<Long> newUserIds) {

        if (!foundUserIds.containsAll(newUserIds)) {
            throw new BusinessException(ErrorCode.USER_INVALID);
        }

        if (newUserIds.contains(currentUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_INVITE_SELF);
        }
    }
}
