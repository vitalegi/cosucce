package it.vitalegi.cosucce.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    Long telegramUserId;

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
