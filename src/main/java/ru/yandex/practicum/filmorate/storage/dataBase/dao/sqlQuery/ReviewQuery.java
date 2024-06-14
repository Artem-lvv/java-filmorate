package ru.yandex.practicum.filmorate.storage.dataBase.dao.sqlQuery;

public class ReviewQuery {
    private ReviewQuery() {
    }

    public static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    public static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id) "
            + "VALUES (?, ?, ?, ?)";
    public static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    public static final String FIND_ALL_AND_SORT_BY_USEFUL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC";
    public static final String FIND_ALL_BY_ID_FILM_AND_SORT_BY_USEFUL_QUERY = "SELECT * FROM reviews WHERE film_id = ?"
                    + "ORDER BY useful DESC LIMIT ?";
    public static final String FIND_ALL_AND_SORT_BY_USEFUL_WITH_COUNT_QUERY = "SELECT * FROM reviews "
                            + "ORDER BY useful DESC LIMIT ?";
    public static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    public static final String UPDATE_USEFUL_QUERY = "UPDATE reviews SET useful = ? WHERE review_id = ?";
    public static final String INSERT_LIKE_QUERY = "INSERT INTO likes_review (review_id, user_id, is_positive) "
                                    + "VALUES (?, ?, ?)";
    public static final String DELETE_LIKE_QUERY = "DELETE FROM likes_review WHERE review_id = ? AND user_id = ?";
    public static final String FIND_BY_ID_USEFUL_QUERY = "SELECT useful FROM reviews WHERE review_id = ?";
    public static final String FAILED_TO_UPDATE_DATA = "Failed to update data";
    public static final String FAILED_TO_DELETE_DATA = "Failed to update data";
}
