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
    (1, 'John', 'Doe', 'John.Doe', '$2a$10$9xC39jTnJaiezRhlCMK66OMJGLf4kWqfEBEbyjFslBqKw9sZiPlbC', true),               -- password123
    (2, 'Jane', 'Smith', 'Jane.Smith', '$2a$10$Vh3T/gkR7ninVal07ZJhMO9Wf0QvJaPnLYh09lGJB30BesYWSj05e', true),           -- password456
    (3, 'Emily', 'Johnson', 'Emily.Johnson', '$2a$10$4/yooYQcQgPMvGODniXeoO2OWkIn11mn2moy2sljxN8DK.TaOxL5u', true),     -- password789
    (4, 'Michael', 'Brown', 'Michael.Brown', '$2a$10$ZbJQjyhqHroOe5ireV/k4Od/wpX09z9fJUjBzCOwpTcdpjSkmDsEG', true),     -- password321
    (5, 'Sarah', 'Davis', 'Sarah.Davis', '$2a$10$DghOi25SCxffM7/OQh0lJeeLrjzUbgIX74dm7POdPviTma7zq4Qbi', true),         -- password654
    (6, 'David', 'Wilson', 'David.Wilson', '$2a$10$CxNBfgKEHcDVzPuZ5F7SIuNroP80yx2FM/tDIBA9ZYdTUxHokZAbi', true),       -- password987
    (7, 'Laura', 'Miller', 'Laura.Miller', '$2a$10$UJmztYys/QXHiQzNzDWdKeFKm/jVv8T4/JtuFx6C38E3h2mF0OVRG', true),       -- password111
    (8, 'James', 'Taylor', 'James.Taylor', '$2a$10$5PO1jnpCFc4iXNMLKNDt5OajLgLM2gslxfkXph5yKBWnPIDu7Y8U6', true);       -- password222

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

INSERT INTO jwt_tokens VALUES
    ('11111111-1111-1111-1111-111111111111', 'ACCESS', '2024-07-01 12:00', 'John.Doe', false),
    ('22222222-2222-2222-2222-222222222222', 'REFRESH', '2024-07-15 12:00', 'John.Doe', false),
    ('33333333-3333-3333-3333-333333333333', 'ACCESS', '2024-07-01 12:00', 'Jane.Smith', false),
    ('44444444-4444-4444-4444-444444444444', 'REFRESH', '2024-07-15 12:00', 'Jane.Smith', false),
    ('55555555-5555-5555-5555-555555555555', 'ACCESS', '2024-07-01 12:00', 'Emily.Johnson', false),
    ('66666666-6666-6666-6666-666666666666', 'REFRESH', '2024-07-15 12:00', 'Emily.Johnson', false),
    ('77777777-7777-7777-7777-777777777777', 'ACCESS', '2024-07-01 12:00', 'Michael.Brown', false),
    ('88888888-8888-8888-8888-888888888888', 'REFRESH', '2024-07-15 12:00', 'Michael.Brown', false);

INSERT INTO user_security VALUES
    (1, false, 0, NULL),
    (2, true, 4, '2026-06-15 12:00:00');

SELECT SETVAL('training_types_id_seq', 8, true);
SELECT SETVAL('users_id_seq', 8, true);
SELECT SETVAL('trainees_id_seq', 4, true);
SELECT SETVAL('trainers_id_seq', 8, true);
SELECT SETVAL('trainings_id_seq', 8, true);
SELECT SETVAL('trainees_to_trainers_id_seq', 4, true);
