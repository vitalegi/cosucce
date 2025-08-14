package it.vitalegi.cosucce.iam.service;

import it.vitalegi.cosucce.budget.constants.UserStatus;
import it.vitalegi.cosucce.iam.entity.IamIdpEntity;
import it.vitalegi.cosucce.iam.entity.IamIdpId;
import it.vitalegi.cosucce.iam.entity.UserEntity;
import it.vitalegi.cosucce.iam.model.UserIdentity;
import it.vitalegi.cosucce.iam.repository.IamIdpRepository;
import it.vitalegi.cosucce.iam.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    IamIdpRepository iamIdpRepository;

    @Transactional
    public UserIdentity build(String issuer, String externalUserId) {
        var idpEntry = iamIdpRepository.findById(new IamIdpId(issuer, externalUserId));
        UserEntity user;
        if (idpEntry.isPresent()) {
            var userId = idpEntry.get().getUserId();
            var opt = userRepository.findById(userId);
            if (opt.isPresent()) {
                user = opt.get();
            } else {
                throw new RuntimeException("No user available for " + issuer + " / " + externalUserId);
            }
        } else {
            user = createNewUser(issuer, externalUserId);
        }
        return toUserIdentity(user);
    }

    protected UserEntity createNewUser(String issuer, String externalUserId) {
        var ts = Instant.now();
        log.info("Create user for {} / {}", issuer, externalUserId);
        var user = new UserEntity();
        user.setUsername("Pinco?!");
        user.setCreationDate(ts);
        user.setLastUpdate(ts);
        user.setStatus(UserStatus.ACTIVE.name());
        user = userRepository.save(user);
        var userId = user.getUserId();
        log.info("Created user {}", userId);

        var iamIdp = new IamIdpEntity();
        iamIdp.setId(new IamIdpId(issuer, externalUserId));
        iamIdp.setUserId(userId);
        iamIdp.setCreationDate(ts);
        iamIdp.setLastUpdate(ts);
        iamIdpRepository.save(iamIdp);

        return user;
    }

    protected UserIdentity toUserIdentity(UserEntity entity) {
        return UserIdentity.builder().id(entity.getUserId()).status(userStatus(entity.getStatus())).build();
    }

    protected UserStatus userStatus(String status) {
        return UserStatus.valueOf(status);
    }
}
