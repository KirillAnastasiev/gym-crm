INSERT INTO training_types (id, training_type_name) VALUES
    (1, 'Fitness'),
    (2, 'Yoga'),
    (3, 'Zumba'),
    (4, 'Stretching'),
    (5, 'Crossfit'),
    (6, 'Pilates'),
    (7, 'Cardio'),
    (8, 'Resistance');

INSERT INTO users (id, first_name, last_name, username, password, is_active) VALUES
    (1, 'John', 'Doe', 'John.Doe', 'password123', true),
    (2, 'Jane', 'Smith', 'Jane.Smith', 'password456', true),
    (3, 'Emily', 'Johnson', 'Emily.Johnson', 'password789', true),
    (4, 'Michael', 'Brown', 'Michael.Brown', 'password321', true),
    (5, 'Sarah', 'Davis', 'Sarah.Davis', 'password654', true),
    (6, 'David', 'Wilson', 'David.Wilson', 'password987', true),
    (7, 'Laura', 'Miller', 'Laura.Miller', 'password111', true),
    (8, 'James', 'Taylor', 'James.Taylor', 'password222', true);

INSERT INTO trainees (id, date_of_birth, address) VALUES
    (1, '1990-01-01', '123 Main St'),
    (2, '1985-05-15', '456 Elm St'),
    (3, '1992-09-30', '789 Oak St'),
    (4, '1988-12-20', '321 Pine St');

INSERT INTO trainers (id, training_type_id) VALUES
    (5, 1),
    (6, 2),
    (7, 3),
    (8, 4);

INSERT INTO trainings (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES
    (1, 1, 5, 'Morning Fitness', 1, '2024-07-01 08:00:00', 12960),
    (2, 2, 6, 'Evening Yoga', 2, '2024-07-02 15:30:00', 9720),
    (3, 3, 7, 'Afternoon Zumba', 3, '2024-07-03 18:00:00', 10800),
    (4, 4, 8, 'Night Stretching', 4, '2024-07-04 12:00:00', 6480),
    (5, 1, 5, 'Weekend Crossfit', 5, '2024-07-05 12:30:00', 19440),
    (6, 2, 6, 'Morning Pilates', 6, '2024-07-06 08:00:00', 19440),
    (7, 3, 7, 'Evening Cardio', 7, '2024-07-07 18:00:00', 9720),
    (8, 4, 8, 'Afternoon Resistance', 8, '2024-07-08 15:30:00', 10800);

INSERT INTO trainees_to_trainers (trainee_id, trainer_id) VALUES
    (1, 5),
    (2, 6),
    (3, 7),
    (4, 8);

INSERT INTO jwt_tokens VALUES
    ('11111111-1111-1111-1111-111111111111', 'ACCESS', '2024-07-01 12:00', 'John.Doe', false),
    ('22222222-2222-2222-2222-222222222222', 'REFRESH', '2024-07-15 12:00', 'John.Doe', false),
    ('33333333-3333-3333-3333-333333333333', 'ACCESS', '2024-07-01 12:00', 'Jane.Smith', false),
    ('44444444-4444-4444-4444-444444444444', 'REFRESH', '2024-07-15 12:00', 'Jane.Smith', false),
    ('55555555-5555-5555-5555-555555555555', 'ACCESS', '2024-07-01 12:00', 'Emily.Johnson', false),
    ('66666666-6666-6666-6666-666666666666', 'REFRESH', '2024-07-15 12:00', 'Emily.Johnson', false),
    ('77777777-7777-7777-7777-777777777777', 'ACCESS', '2024-07-01 12:00', 'Michael.Brown', false),
    ('88888888-8888-8888-8888-888888888888', 'REFRESH', '2024-07-15 12:00', 'Michael.Brown', false);