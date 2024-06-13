package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

public class MpaQuery {
    public static final String FIND_BY_ID = "SELECT * FROM MOTION_PICTURE_ASSOCIATION WHERE ID = ?";
    public static final String FIND_ALL = "SELECT * FROM MOTION_PICTURE_ASSOCIATION";
    public static final String FIND_MPA_BY_FILM_ID = "SELECT * FROM MOTION_PICTURE_ASSOCIATION " +
            "WHERE ID = (SELECT MPA_ID FROM FILM_MPA WHERE FILM_ID = ? GROUP BY MPA_ID)";
    public static final String FIND_RECORD_BY_FILM_ID_AND_MPA_ID = "SELECT * FROM FILM_MPA " +
            "WHERE FILM_ID = ? AND MPA_ID = ?";
    public static final String ADD_FILM_MPA = "INSERT INTO FILM_MPA (FILM_ID, MPA_ID) VALUES (?, ?)";
    public static final String DELETE_FILM_MPA = "DELETE FROM FILM_MPA WHERE FILM_ID = ?";
}
