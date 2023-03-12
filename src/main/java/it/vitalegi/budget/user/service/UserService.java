package it.vitalegi.budget.user.service;

import it.vitalegi.budget.auth.AuthenticationService;
import it.vitalegi.budget.metrics.Performance;
import it.vitalegi.budget.metrics.Type;
import it.vitalegi.budget.user.constant.OtpStatus;
import it.vitalegi.budget.user.dto.OtpResponse;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.dto.UserOtp;
import it.vitalegi.budget.user.entity.UserEntity;
import it.vitalegi.budget.user.entity.UserOtpEntity;
import it.vitalegi.budget.user.mapper.UserMapper;
import it.vitalegi.budget.user.repository.UserOtpRepository;
import it.vitalegi.budget.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Performance(Type.SERVICE)
@Log4j2
@Service
public class UserService {

    @Value("${user.otp.duration}")
    int otpDuration;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserOtpRepository userOtpRepository;

    @Autowired
    UserMapper mapper;

    public UserOtp addOtp() {
        var user = getCurrentUserEntity();
        UserOtpEntity entity = new UserOtpEntity();
        entity.setUser(user);
        entity.setValidTo(LocalDateTime.now().plusSeconds(otpDuration));
        entity.setOtp(UUID.randomUUID().toString());
        entity.setStatus(OtpStatus.ACTIVE.name());
        var out = userOtpRepository.save(entity);
        log.info("Created new OTP for user {}", user.getId());
        return mapper.map(out);
    }

    public User getCurrentUser() {
        return mapper.map(getCurrentUserEntity());
    }

    public UserEntity getCurrentUserEntity() {
        String uid = authenticationService.getUid();
        UserEntity user = userRepository.findByUid(uid);
        if (user == null) {
            throw new NoSuchElementException("current user " + uid + " doesn't have a corresponding db entity");
        }
        return user;
    }

    public UserEntity getUserEntity(long id) {
        return userRepository.findById(id).get();
    }

    @Transactional
    public void importCurrentUser() {
        String id = authenticationService.getUid();
        if (userRepository.findByUid(id) != null) {
            return;
        }
        log.info("User {} is unknown, import.", id);
        UserEntity user = new UserEntity();
        user.setUid(id);
        user.setUsername(authenticationService.getName());
        userRepository.save(user);
    }
    public User updateTelegramUserId(long userId, long telegramUserId) {
        UserEntity user = getUserEntity(userId);
        user.setTelegramUserId(telegramUserId);
        user = userRepository.save(user);
        return mapper.map(user);
    }

    @Transactional
    public User updateUsername(String username) {
        UserEntity user = getCurrentUserEntity();
        user.setUsername(username);
        return mapper.map(userRepository.save(user));
    }

    @Transactional
    public OtpResponse useOtp(Long userId, String otp) {
        var entries = userOtpRepository.findByOtp(otp);

        UserOtpEntity entry = entries.stream().filter(this::acceptOtpByDate).filter(this::acceptOtpByStatus)
                                     .filter(e -> acceptOtpByUser(e, userId)).findFirst().orElse(null);

        if (entry == null) {
            log.info("OTP not found for this user");
            return OtpResponse.ko();
        }
        log.info("OTP is valid, revoke it");
        updateOtpStatus(entry.getId(), OtpStatus.USED);
        return OtpResponse.ok(mapper.map(entry));
    }

    protected boolean acceptOtpByDate(UserOtpEntity entry) {
        LocalDateTime now = LocalDateTime.now();
        if (entry.getValidTo().isBefore(now)) {
            log.info("OTP is expired. Now: {}, OTP valid until: {}", now, entry.getValidTo());
            return false;
        }
        return true;
    }

    protected boolean acceptOtpByStatus(UserOtpEntity entry) {
        if (!OtpStatus.ACTIVE.name().equals(entry.getStatus())) {
            log.info("OTP isn't active: {}", entry.getStatus());
            return false;
        }
        return true;
    }

    protected boolean acceptOtpByUser(UserOtpEntity entry, Long userId) {
        if (userId == null) {
            return true;
        }
        long actualId = entry.getUser().getId();
        if (!userId.equals(actualId)) {
            log.info("OTP is assigned to a different user. Expected: {}, Actual: {}", userId, actualId);
            return false;
        }
        return true;
    }

    protected User getUserByTelegramId(long telegramUserId) {
        UserEntity user = userRepository.findByTelegramUserId(telegramUserId);
        if (user == null) {
            return null;
        }
        return mapper.map(user);
    }

    protected UserOtpEntity updateOtpStatus(long id, OtpStatus status) {
        var otp = userOtpRepository.findById(id);
        if (otp.isEmpty()) {
            throw new IllegalArgumentException("OTP " + id + " not found");
        }
        var entry = otp.get();
        entry.setStatus(status.name());
        return userOtpRepository.save(entry);
    }
}
