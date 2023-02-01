package it.vitalegi.budget.board.entity;

import it.vitalegi.budget.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
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
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "BoardSplit")
@Table(name = "board_split")
public class BoardSplitEntity {
    @Id
    @GeneratedValue
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_split__user__owner_id"))
    UserEntity user;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_split__board__board_id"))
    BoardEntity board;
    Integer fromYear;
    Integer fromMonth;
    Integer toYear;
    Integer toMonth;
    BigDecimal value1;

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
