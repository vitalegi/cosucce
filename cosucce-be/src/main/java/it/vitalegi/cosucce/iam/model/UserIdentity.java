package it.vitalegi.cosucce.iam.model;

import it.vitalegi.cosucce.budget.constants.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIdentity {
    UUID id;
    UserStatus status;
    List<String> roles;
}
