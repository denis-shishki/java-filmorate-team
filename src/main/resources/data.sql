MERGE INTO mpa (mpa_id, mpa_name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genres (genre_id, genre_name)
    VALUES (1,'Комедия'),
           (2,'Драма'),
           (3,'Мультфильм'),
           (4,'Триллер'),
           (5,'Документальный'),
           (6,'Боевик');

MERGE INTO operations (operation_id, operation_name)
    VALUES (1,'REMOVE'),
    (2,'ADD'),
    (3,'UPDATE');

MERGE INTO event_types (event_type_id, event_type_name)
    VALUES (1,'LIKE'),
    (2,'REVIEW '),
    (3,'FRIEND');