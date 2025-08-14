package it.vitalegi.cosucce.budget.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Entity(name = "board_account")
@Table(name = "board_account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountId;
    private UUID boardId;
    private String label;
    private String icon;
    private boolean enabled;
    private Instant creationDate;
    private Instant lastUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardAccountEntity that = (BoardAccountEntity) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }
}
