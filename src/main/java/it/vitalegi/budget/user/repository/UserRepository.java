package it.vitalegi.budget.user.repository;

import it.vitalegi.budget.user.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByTelegramUserId(Long telegramUserId);

    UserEntity findByUid(String uid);
}
