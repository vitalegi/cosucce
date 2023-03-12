package it.vitalegi.budget.user.repository;

import it.vitalegi.budget.user.entity.UserOtpEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOtpRepository extends CrudRepository<UserOtpEntity, Long> {

    List<UserOtpEntity> findByUserId(long userId);

    List<UserOtpEntity> findByOtp(String otp);

}
