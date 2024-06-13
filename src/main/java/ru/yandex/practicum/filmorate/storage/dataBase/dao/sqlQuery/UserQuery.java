package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

public class UserQuery {
    private UserQuery() {
    }

    public static final String CREATE_USER = "INSERT INTO USERS (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_USER = "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    public static final String FIND_ALL = "SELECT * FROM USERS ORDER BY ID";
    public static final String FIND_BY_EMAIL = "SELECT * FROM USERS WHERE email = ?";
    public static final String FIND_BY_ID = "SELECT * FROM USERS WHERE id = ?";
    public static final String FIND_BY_LOGIN = "SELECT * FROM USERS WHERE login = ?";
    public static final String FIND_RECOED_BY_USER_ID_AND_FRIEND_ID = "SELECT * FROM USER_FRIENDS WHERE USER_ID = ? and FRIEND_ID = ?";
    public static final String ADD_FRIEND = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
    public static final String FIND_ALL_FRIENDS_USER = "SELECT * FROM USERS " +
            "WHERE ID IN (SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?)";
    public static final String DELETE_FRIEND_USER = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
    public static final String GET_COMMON_FRIENDS_USER = "SELECT *\n" +
            "FROM USERS\n" +
            "WHERE ID IN (\n" +
            "    SELECT uf1.friend_id\n" +
            "    FROM USER_FRIENDS uf1\n" +
            "             JOIN USER_FRIENDS uf2 ON uf1.friend_id = uf2.friend_id\n" +
            "    WHERE uf1.user_id = ? AND uf2.user_id = ?\n" +
            "    )";
    public static final String DELETE_USER = "DELETE FROM USERS WHERE id = ?";
}
