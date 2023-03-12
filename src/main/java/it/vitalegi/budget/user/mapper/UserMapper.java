package it.vitalegi.budget.user.mapper;

import it.vitalegi.budget.user.constant.OtpStatus;
import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.dto.UserOtp;
import it.vitalegi.budget.user.entity.UserEntity;
import it.vitalegi.budget.user.entity.UserOtpEntity;
import it.vitalegi.budget.util.ObjectUtil;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User map(UserEntity source) {
        User dto = new User();
        dto.setId(source.getId());
        dto.setUsername(source.getUsername());
        dto.setUid(source.getUid());
        dto.setTelegramUserId(source.getTelegramUserId());
        return dto;
    }

    public UserEntity map(User source) {
        return ObjectUtil.copy(source, new UserEntity());
    }

    public UserOtp map(UserOtpEntity entity) {
        if (entity == null) {
            return null;
        }
        UserOtp dto = new UserOtp();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setValidTo(entity.getValidTo());
        dto.setOtp(entity.getOtp());
        dto.setStatus(map(entity.getStatus()));
        return dto;
    }

    public OtpStatus map(String status) {
        if (status == null) {
            return null;
        }
        return OtpStatus.valueOf(status);
    }
}
