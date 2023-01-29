package it.vitalegi.budget.board.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BoardInvite {
    UUID id;
    UUID boardId;
    long ownerId;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
}
