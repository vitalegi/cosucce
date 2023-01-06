package it.vitalegi.budget.exception;

import lombok.Data;

@Data
public class PermissionException extends RuntimeException {
    String entity;
    String id;
    String permission;

    public PermissionException(String entity, String id, String permission) {
        super();
        this.entity = entity;
        this.id = id;
        this.permission = permission;
    }
}
