INSERT INTO trainings (id, trainer_username, trainer_first_name, trainer_last_name, trainer_status, training_date, training_duration) VALUES
    (1, 'Sarah.Davis', 'David', 'Wilson', 'ACTIVE', '2024-07-01 08:00', 129600000000),
    (2, 'David.Wilson', 'Jane', 'Smith', 'ACTIVE', '2024-07-02 15:30', 97200000000),
    (3, 'Laura.Miller', 'Laura', 'Miller', 'ACTIVE', '2024-07-03 18:00', 1080000000000),
    (4, 'James.Taylor', 'James', 'Taylor', 'ACTIVE', '2024-07-04 12:00', 648000000000),
    (5, 'Sarah.Davis', 'David', 'Wilson', 'ACTIVE', '2024-07-05 12:30', 1944000000000),
    (6, 'David.Wilson', 'Jane', 'Smith', 'ACTIVE', '2024-07-06 08:00', 1944000000000),
    (7, 'Laura.Miller', 'Laura', 'Miller', 'ACTIVE', '2024-07-07 18:00', 972000000000),
    (8, 'James.Taylor', 'James', 'Taylor', 'ACTIVE', '2024-07-08 15:30', 1080000000000);

ALTER SEQUENCE trainings_id_seq RESTART WITH 9;