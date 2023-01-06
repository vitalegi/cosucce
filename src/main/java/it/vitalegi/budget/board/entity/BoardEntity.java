package it.vitalegi.budget.board.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "board")
public class BoardEntity {
    @Id
    @GeneratedValue
    UUID id;
    String name;
    String ownerId;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
}
