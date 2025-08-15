package it.vitalegi.cosucce.budget.entity;

import it.vitalegi.cosucce.iam.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity(name = "board_user")
@Table(name = "board_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUserEntity {

    @EmbeddedId
    private BoardUserId id;
    private String role;
    private int version;
    private Instant creationDate;
    private Instant lastUpdate;

    @Column(name = "boardId", insertable = false, updatable = false)
    private UUID boardId;
    @ManyToOne
    @JoinColumn(name = "boardId", referencedColumnName = "boardId", insertable = false, updatable = false)
    private BoardEntity board;

    @Column(name = "userId", insertable = false, updatable = false)
    private UUID userId;
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserEntity user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardUserEntity that = (BoardUserEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BoardUserEntity{" + "id=" + id + ", role='" + role + '\'' + ", creationDate=" + creationDate + ", lastUpdate=" + lastUpdate + '}';
    }
}
