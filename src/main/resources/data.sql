INSERT INTO training_types VALUES
    (1, 'Fitness'),
    (2, 'Yoga'),
    (3, 'Zumba'),
    (4, 'Stretching'),
    (5, 'Crossfit'),
    (6, 'Pilates'),
    (7, 'Cardio'),
    (8, 'Resistance');

INSERT INTO users VALUES
    (1, 'John', 'Doe', 'John.Doe', 'password123', true),
    (2, 'Jane', 'Smith', 'Jane.Smith', 'password456', true),
    (3, 'Emily', 'Johnson', 'Emily.Johnson', 'password789', true),
    (4, 'Michael', 'Brown', 'Michael.Brown', 'password321', true),
    (5, 'Sarah', 'Davis', 'Sarah.Davis', 'password654', true),
    (6, 'David', 'Wilson', 'David.Wilson', 'password987', true),
    (7, 'Laura', 'Miller', 'Laura.Miller', 'password111', true),
    (8, 'James', 'Taylor', 'James.Taylor', 'password222', true);

INSERT INTO trainees VALUES
    (1, '1990-01-01', '123 Main St'),
    (2, '1985-05-15', '456 Elm St'),
    (3, '1992-09-30', '789 Oak St'),
    (4, '1988-12-20', '321 Pine St');

INSERT INTO trainers VALUES
    (5, 1),
    (6, 2),
    (7, 3),
    (8, 4);

INSERT INTO trainings VALUES
    (1, 1, 5, 'Morning Fitness', 1, '2024-07-01 8:00', 3.6E12),
    (2, 2, 6, 'Evening Yoga', 2, '2024-07-02 15:30', 2.7E12),
    (3, 3, 7, 'Afternoon Zumba', 3, '2024-07-03 18:00', 3.0E12),
    (4, 4, 8, 'Night Stretching', 4, '2024-07-04 12:00', 1.8E12),
    (5, 1, 5, 'Weekend Crossfit', 5, '2024-07-05 12:30', 5.4E12),
    (6, 2, 6, 'Morning Pilates', 6, '2024-07-06 8:00', 5.4E12),
    (7, 3, 7, 'Evening Cardio', 7, '2024-07-07 18:00', 2.7E12),
    (8, 4, 8, 'Afternoon Resistance', 8, '2024-07-08 15:30', 3.0E12);

INSERT INTO trainees_to_trainers VALUES
    (1, 1, 5),
    (2, 2, 6),
    (3, 3, 7),
    (4, 4, 8);

SELECT SETVAL('training_types_id_seq', 8, true);
SELECT SETVAL('users_id_seq', 8, true);
SELECT SETVAL('trainees_id_seq', 4, true);
SELECT SETVAL('trainers_id_seq', 8, true);
SELECT SETVAL('trainings_id_seq', 8, true);
SELECT SETVAL('trainees_to_trainers_id_seq', 4, true);
