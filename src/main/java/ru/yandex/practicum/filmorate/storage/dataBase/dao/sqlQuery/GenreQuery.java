package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

public class GenreQuery {
    private GenreQuery() {
    }

    public static final String FIND_ALL = "SELECT * FROM GENRE";
    public static final String FIND_BY_ID = "SELECT * FROM GENRE WHERE ID = ?";
    public static final String FIND_RECORD_BY_FILM_ID_AND_GENRE_ID = "SELECT * FROM FILM_GENRE " +
            "WHERE FILM_ID = ? AND GENRE_ID = ?";
    public static final String ADD_FILM_GENRE = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
    public static final String FIND_GENRES_BY_FILM_ID = "SELECT * FROM GENRE" +
            " WHERE ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ? GROUP BY GENRE_ID) ORDER BY ID";
    public static final String DELETE_FILM_GENRE = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
}
