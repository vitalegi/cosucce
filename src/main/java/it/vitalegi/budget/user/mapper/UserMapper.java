package it.vitalegi.budget.user.mapper;

import it.vitalegi.budget.user.dto.User;
import it.vitalegi.budget.user.entity.UserEntity;
import it.vitalegi.budget.util.ObjectUtil;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User map(UserEntity source) {
        return ObjectUtil.copy(source, new User());
    }

    public UserEntity map(User source) {
        return ObjectUtil.copy(source, new UserEntity());
    }
}
