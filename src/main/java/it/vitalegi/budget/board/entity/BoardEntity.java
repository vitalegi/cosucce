package it.vitalegi.budget.board.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;

    @OneToMany(mappedBy = "board")
    Set<BoardEntryEntity> boardEntries;

    @OneToMany(mappedBy = "board")
    Set<BoardUserEntity> boardUsers;

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
        return this.getClass()
                   .getName() + "(" + getId() + ")";
    }
}
