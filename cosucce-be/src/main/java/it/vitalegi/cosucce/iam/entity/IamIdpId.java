package it.vitalegi.cosucce.iam.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class IamIdpId {
    private String issuer;
    private String subject;

    public IamIdpId(String issuer, String subject) {
        this.issuer = issuer;
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IamIdpId iamIdpId = (IamIdpId) o;
        return Objects.equals(issuer, iamIdpId.issuer) && Objects.equals(subject, iamIdpId.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuer, subject);
    }

    @Override
    public String toString() {
        return "IamIdpId{" + "issuer='" + issuer + '\'' + ", subject='" + subject + '\'' + '}';
    }
}
