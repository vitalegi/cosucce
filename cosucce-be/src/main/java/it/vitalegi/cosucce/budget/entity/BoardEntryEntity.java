package it.vitalegi.cosucce.budget.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "board_entry")
@Table(name = "board_entry")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID entryId;
    private UUID boardId;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private UUID lastUpdatedBy;
    private Instant creationDate;
    private Instant lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardEntryEntity that = (BoardEntryEntity) o;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entryId);
    }
}
