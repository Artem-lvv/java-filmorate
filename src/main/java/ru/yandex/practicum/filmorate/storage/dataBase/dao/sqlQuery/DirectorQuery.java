package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

public class DirectorQuery {
    private DirectorQuery() {
    }

    public static final String FIND_ALL = "SELECT * FROM DIRECTOR";
    public static final String FIND_BY_ID = "SELECT * FROM DIRECTOR WHERE ID = ?";
    public static final String CREATE_DIRECTOR = "INSERT INTO DIRECTOR (name) VALUES (?)";
    public static final String UPDATE_DIRECTOR = "UPDATE DIRECTOR SET name = ? WHERE id = ?";
    public static final String FIND_RECORD_BY_FILM_ID_AND_DIRECTOR_ID = "SELECT * FROM FILM_DIRECTOR WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
    public static final String ADD_FILM_DIRECTOR = "INSERT INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
    public static final String FIND_DIRECTORS_BY_FILM_ID = "SELECT * FROM DIRECTOR" +
            " WHERE ID IN (SELECT DIRECTOR_ID FROM FILM_DIRECTOR WHERE FILM_ID = ?) ORDER BY ID";
    public static final String DELETE_DIRECTOR = "DELETE FROM DIRECTOR WHERE ID = ?";
    public static final String DELETE_FILM_DIRECTOR = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
    public static final String DELETE_DIRECTOR_BY_FILM_ID = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?;";
    public static final String UPDATE_FILM_DIRECTORS = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID) VALUES(?, ?);";
}
