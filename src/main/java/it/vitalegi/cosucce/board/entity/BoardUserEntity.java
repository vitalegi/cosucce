package it.vitalegi.cosucce.board.entity;

import it.vitalegi.cosucce.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity(name = "BoardUser")
@Table(name = "board_user")
public class BoardUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_user__board__board_id"))
    BoardEntity board;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_user__user__user_id"))
    UserEntity user;

    @NotNull String role;

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BoardUserEntity other = (BoardUserEntity) obj;
        return Objects.equals(id, other.getId());
    }

    @Override
    public String toString() {
        return this.getClass()
                   .getName() + "(" + getId() + ")";
    }

}
