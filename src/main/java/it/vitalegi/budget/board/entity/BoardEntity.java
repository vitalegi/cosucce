package it.vitalegi.budget.board.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name="board")
public class BoardEntity {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    UUID id;
    String name;
    String ownerId;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
}
