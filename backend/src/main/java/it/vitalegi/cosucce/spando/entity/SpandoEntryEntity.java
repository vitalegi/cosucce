package it.vitalegi.cosucce.spando.entity;

import it.vitalegi.cosucce.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "SpandoEntry")
@Table(name = "spando_entry")
public class SpandoEntryEntity {
    @Id
    @GeneratedValue
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    UUID id;
    @NotNull
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk__spando_entry__user__user_id"))
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    UserEntity user;

    @NotNull LocalDate entryDate;
    @NotNull String type;

    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SpandoEntryEntity other = (SpandoEntryEntity) obj;
        return Objects.equals(id, other.getId());
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + getId() + ")";
    }

}
