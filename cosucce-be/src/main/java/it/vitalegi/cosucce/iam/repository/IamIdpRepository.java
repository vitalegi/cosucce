package it.vitalegi.cosucce.iam.repository;

import it.vitalegi.cosucce.iam.entity.IamIdpEntity;
import it.vitalegi.cosucce.iam.entity.IamIdpId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IamIdpRepository extends JpaRepository<IamIdpEntity, IamIdpId> {

}
