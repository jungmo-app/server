package jungmo.server.global.aop.aspect;

import jungmo.server.domain.entity.Authority;
import jungmo.server.domain.entity.Gathering;
import jungmo.server.domain.entity.User;
import jungmo.server.domain.provider.GatheringDataProvider;
import jungmo.server.domain.provider.UserDataProvider;
import jungmo.server.domain.repository.GatheringUserRepository;
import jungmo.server.domain.service.GatheringService;
import jungmo.server.global.error.ErrorCode;
import jungmo.server.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final GatheringDataProvider gatheringDataProvider;
    private final UserDataProvider userDataProvider;
    private final GatheringUserRepository gatheringUserRepository;

    @Transactional(readOnly = true)
    @Before("@annotation(jungmo.server.global.aop.annotation.CheckWritePermission) && args(gatheringId,..)")
    public void checkWritePermission(Long gatheringId) {
        User user = userDataProvider.getUser();

        Gathering gathering = gatheringDataProvider.findGathering(gatheringId);
        validateWriteAuthority(user, gathering); // 권한 검사
    }

    private void validateWriteAuthority(User user, Gathering gathering) {
        gatheringUserRepository.findByAuthority(user, gathering, Authority.WRITE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_AUTHORITY));
    }
}
