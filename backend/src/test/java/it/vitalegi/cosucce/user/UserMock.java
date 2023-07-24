package it.vitalegi.cosucce.user;

import it.vitalegi.cosucce.user.entity.UserEntity;

public class UserMock {

    private static long ID = 1;

    public static UserEntity createUser(long userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(randomName());
        return user;
    }

    protected static String randomName() {
        return "NAME_" + (ID++);
    }

}
