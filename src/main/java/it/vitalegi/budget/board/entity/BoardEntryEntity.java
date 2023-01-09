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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "BoardEntry")
@Table(name = "board_entry")
public class BoardEntryEntity {
    @Id
    @GeneratedValue
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_entry__user__owner_id"))
    UserEntity owner;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__board_entry__board__board_id"))
    BoardEntity board;

    @NotNull
    LocalDate date;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
    @NotNull
    String category;
    String description;
    @NotNull
    BigDecimal amount;
}