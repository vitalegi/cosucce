package it.vitalegi.cosucce.exception;

import lombok.Data;

@Data
public class PermissionException extends RuntimeException {
    String entity;
    String id;
    String permission;

    public PermissionException(String entity, String id, String permission) {
        super("Missing permission " + permission + " to work on entity " + entity);
        this.entity = entity;
        this.id = id;
        this.permission = permission;
    }
}
