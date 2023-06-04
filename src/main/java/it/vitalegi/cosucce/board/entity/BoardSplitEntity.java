package it.vitalegi.cosucce.board.entity;

import it.vitalegi.cosucce.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

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
    @JdbcTypeCode(java.sql.Types.VARCHAR)
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
