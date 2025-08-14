package it.vitalegi.cosucce.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "iam_idp")
@Table(name = "iam_idp")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamIdpEntity {

    @EmbeddedId
    private IamIdpId id;
    private UUID userId;
    private Instant creationDate;
    private Instant lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IamIdpEntity that = (IamIdpEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    @Override
    public String toString() {
        return "IamIdpEntity{" + "id=" + id + ", userId=" + userId + ", creationDate=" + creationDate + ", lastUpdate=" + lastUpdate + '}';
    }
}
