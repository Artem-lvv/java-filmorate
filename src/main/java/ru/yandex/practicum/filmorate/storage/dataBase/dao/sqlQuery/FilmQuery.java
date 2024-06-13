package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

import java.util.List;
import java.util.Map;

public class FilmQuery {
    private FilmQuery() {
    }

    public static final String CREATE_FILM = "INSERT INTO FILM (name, description, release_date, duration) " +
            "VALUES (?, ?, ?, ?)";

    public static final String UPDATE_FILM = "UPDATE FILM SET name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE id = ?";

    public static final String FIND_ALL = "SELECT * FROM FILM";

    public static final String ADD_LIKE_FILM = "SELECT * FROM FILM_LIKES WHERE FILM_ID = ? and USER_ID = ?";

    public static final String DELETE_LIKE_FILM = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    public static final String FIND_BY_NAME = "SELECT * FROM FILM WHERE name = ?";

    public static final String FIND_BY_ID = "SELECT * FROM FILM WHERE id = ?";

    public static final String FIND_ALL_COMMON_FILMS = "SELECT f.*, " +
            "COUNT(l3.film_id) FROM film AS f " +
            "LEFT JOIN film_likes AS l1 ON f.id = l1.film_id " +
            "LEFT JOIN users AS u1 ON l1.user_id = u1.id " +
            "LEFT JOIN film_likes AS l2 ON l1.film_id = l2.film_id " +
            "LEFT JOIN users AS u2 ON l2.user_id = u2.id " +
            "LEFT JOIN film_likes AS l3 ON f.id = l3.film_id " +
            "WHERE u1.id = ? AND u2.id = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(l3.film_id) DESC, f.id";
    public static final String DELETE_FILM = "DELETE FROM FILM WHERE id = ?";
    public static final String FIND_RECORD_BY_ID_AND_USER_ID = "INSERT INTO FILM_LIKES (film_id, user_id) VALUES (?, ?)";

    public static void getQueryForFindPopularFilmsBySelection(StringBuilder sqlQuery, List<Object> paramsResult,
                                                              Map<String, Long> allParams) {
        sqlQuery.append(
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "COUNT(fl.user_id) AS likes_count " +
                        "FROM film f " +
                        "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                        "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                        "WHERE 1=1 "
        );

        if (allParams.containsKey("genreId")) {
            sqlQuery.append("AND fg.genre_id = ? ");
            paramsResult.add(allParams.get("genreId"));
        }

        if (allParams.containsKey("year")) {
            sqlQuery.append("AND EXTRACT(YEAR FROM f.release_date) = ? ");
            paramsResult.add(allParams.get("year"));
        }

        sqlQuery.append("GROUP BY f.id, f.name, f.description, f.release_date, f.duration " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?");
        paramsResult.add(allParams.get("count"));
    }

    public static final String FIND_SIMILAR_USERS_BY_LIKES = "SELECT " +
            "uf1.user_id, COUNT(*) AS common_likes " +
            "FROM FILM_LIKES AS uf1 " +
            "JOIN FILM_LIKES AS uf2 ON uf1.film_id = uf2.film_id " +
            "WHERE uf2.user_id = ? AND uf1.user_id != ? " +
            "GROUP BY uf1.user_id " +
            "ORDER BY common_likes DESC";

    public static final String FIND_RECOMMENDED_FILMS = "SELECT * " +
            "FROM FILM_LIKES AS fl " +
            "JOIN FILM AS f ON fl.film_id = f.id " +
            "WHERE user_id = ? " +
            "AND film_id NOT IN (SELECT film_id FROM FILM_LIKES WHERE user_id = ?)";

    public static String getQueryForFindDirectorFilms(String sortBy) {
        StringBuilder sqlQuery = new StringBuilder(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration FROM FILM AS f\n" +
                        "JOIN FILM_DIRECTOR AS fd ON f.ID = fd.FILM_ID\n" +
                        "LEFT JOIN (\n" +
                        "SELECT film_id, count(*) likes_count\n" +
                        "FROM FILM_LIKES\n" +
                        "GROUP BY film_id\n" +
                        ") fl ON fl.film_id = f.ID\n" +
                        "WHERE fd.DIRECTOR_ID = ?\n");

        if (sortBy.equals("year")) {
            sqlQuery.append("ORDER BY f.release_date\n");
        } else if (sortBy.equals("likes")) {
            sqlQuery.append("ORDER BY fl.likes_count DESC\n");
        }

        return sqlQuery.toString();
    }

    public static void getQueryForSearchFilm(StringBuilder sqlQuery, String query, String searchBy,
                                             List<Object> paramsResult) {
        sqlQuery.append(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration FROM FILM AS f\n" +
                        "LEFT JOIN FILM_DIRECTOR AS fd ON f.ID = fd.FILM_ID\n" +
                        "LEFT JOIN DIRECTOR AS d ON d.ID = fd.DIRECTOR_ID\n" +
                        "LEFT JOIN (\n" +
                        "SELECT film_id, count(*) likes_count\n" +
                        "FROM FILM_LIKES\n" +
                        "GROUP BY film_id\n" +
                        ") fl ON fl.film_id = f.ID\n" +
                        "WHERE \n"
        );

        String searchStr = "%" + query.toLowerCase() + "%";
        if (searchBy.contains("director")) {
            sqlQuery.append("lower(d.NAME) LIKE ?\n");
            paramsResult.add(searchStr);
        }
        if (searchBy.contains("director") && searchBy.contains("title")) {
            sqlQuery.append("OR\n");
        }
        if (searchBy.contains("title")) {
            sqlQuery.append("lower(f.name) LIKE ?\n");
            paramsResult.add(searchStr);
        }
        sqlQuery.append("ORDER BY fl.likes_count DESC\n");
    }

}
