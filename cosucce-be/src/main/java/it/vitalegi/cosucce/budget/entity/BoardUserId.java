package it.vitalegi.cosucce.budget.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class BoardUserId implements Serializable {
    private UUID boardId;
    private UUID userId;

    public BoardUserId() {
    }

    public BoardUserId(UUID boardId, UUID userId) {
        this.boardId = boardId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardUserId that = (BoardUserId) o;
        return Objects.equals(boardId, that.boardId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, userId);
    }

    @Override
    public String toString() {
        return "BoardUserId{" + "boardId=" + boardId + ", userId=" + userId + '}';
    }
}
