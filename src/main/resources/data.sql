MERGE
INTO
	GENRE t
		USING (
VALUES
		(1, 'Комедия'),
		(2, 'Драма'),
		(3, 'Мультфильм'),
		(4, 'Триллер'),
		(5, 'Документальный'),
		(6, 'Боевик')
	) S(ID, NAME)
 ON
	t.NAME = s.NAME
	WHEN NOT MATCHED THEN
INSERT (ID, NAME)
VALUES (S.ID, S.NAME);

MERGE
INTO
	MOTION_PICTURE_ASSOCIATION t
		USING (
VALUES
	    (1, 'G'),
		(2, 'PG'),
		(3, 'PG-13'),
		(4, 'R'),
		(5, 'NC-17')
	) S(ID, NAME)
 ON
	t.NAME = s.NAME
	WHEN NOT MATCHED THEN
INSERT (ID, NAME)
VALUES (S.ID, S.NAME);
