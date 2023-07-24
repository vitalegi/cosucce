package it.vitalegi.cosucce.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "Board")
@Table(name = "board")
public class BoardEntity {
    @Id
    @GeneratedValue
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    UUID id;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    Set<BoardEntryEntity> boardEntries;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    Set<BoardUserEntity> boardUsers;
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    Set<BoardInviteEntity> boardInvites;

    @NotNull String name;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BoardEntity other = (BoardEntity) obj;
        return Objects.equals(id, other.getId());
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + getId() + ")";
    }
}
