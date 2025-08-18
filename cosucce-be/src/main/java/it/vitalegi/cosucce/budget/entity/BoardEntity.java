package it.vitalegi.cosucce.budget.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "board")
@Table(name = "board")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity {

    @Id
    private UUID boardId;
    private String name;
    private String etag;
    private Instant creationDate;
    private Instant lastUpdate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.boardId")
    private Set<BoardUserEntity> boardUsers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardEntity that = (BoardEntity) o;
        return Objects.equals(boardId, that.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(boardId);
    }

}
