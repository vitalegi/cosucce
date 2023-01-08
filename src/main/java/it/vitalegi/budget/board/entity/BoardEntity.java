package it.vitalegi.budget.board.entity;

import it.vitalegi.budget.user.entity.UserEntity;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
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

    @NotNull
    String name;
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board__user__owner_id"))
    UserEntity owner;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
}
