package it.vitalegi.cosucce.iam.entity;

import it.vitalegi.cosucce.budget.entity.BoardUserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "user_data")
@Table(name = "user_data")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    private String username;
    private String status;
    private Instant creationDate;
    private Instant lastUpdate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userId")
    private Set<BoardUserEntity> boardUsers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    @Override
    public String toString() {
        return "UserEntity{" + "userId=" + userId + ", username='" + username + '\'' + ", status=" + status + ", creationDate=" + creationDate + ", lastUpdate=" + lastUpdate + '}';
    }
}
