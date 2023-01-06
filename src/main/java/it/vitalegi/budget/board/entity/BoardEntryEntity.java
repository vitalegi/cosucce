package it.vitalegi.budget.board.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "BoardEntry")
@Table(name = "BoardEntry")
public class BoardEntryEntity {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;

    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID boardId;
    LocalDate date;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
    String ownerId;
    String category;
    String description;
    BigDecimal amount;
}
