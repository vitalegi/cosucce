package it.vitalegi.budget.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity(name = "User")
@Table(name = "user_data")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String uid;
    String username;

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UserEntity other = (UserEntity) obj;
        return Objects.equals(id, other.getId());
    }
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + getId() + ")";
    }
}
