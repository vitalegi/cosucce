package it.vitalegi.budget.user;

import it.vitalegi.budget.user.entity.UserEntity;

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
