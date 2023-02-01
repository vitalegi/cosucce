package it.vitalegi.budget.board.entity;

import it.vitalegi.budget.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "BoardEntry")
@Table(name = "board_entry")
public class BoardEntryEntity {
    @Id
    @GeneratedValue
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;
    @NotNull
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_entry__user__owner_id"))
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    UserEntity owner;

    @NotNull
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_entry__board__board_id"))
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    BoardEntity board;

    @NotNull LocalDate date;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
    @NotNull String category;
    String description;
    @NotNull BigDecimal amount;

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
        BoardEntryEntity other = (BoardEntryEntity) obj;
        return Objects.equals(id, other.getId());
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + getId() + ")";
    }

}
