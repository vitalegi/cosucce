package it.vitalegi.cosucce.board.constant;

public enum BoardUserRole {
    OWNER,
    MEMBER;

    public enum BoardGrant {
        BOARD_VIEW, BOARD_DELETE, BOARD_EDIT, BOARD_ENTRY_EDIT, BOARD_USER_ROLE_EDIT, BOARD_MANAGE_MEMBER,
        BOARD_ENTRY_IMPORT
    }
}
