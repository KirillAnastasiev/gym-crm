DROP TABLE IF EXISTS trainees_to_trainers;
DROP TABLE IF EXISTS trainings;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS trainees;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS training_types;

CREATE TABLE IF NOT EXISTS training_types (
    id                      SERIAL                              NOT NULL                            UNIQUE,
    training_type_name      VARCHAR(50)                         NOT NULL                            UNIQUE,
    CONSTRAINT              training_types_pk                   PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id                      SERIAL                              NOT NULL                            UNIQUE,
    first_name              VARCHAR(50)                         NOT NULL,
    last_name               VARCHAR(50)                         NOT NULL,
    username                VARCHAR(110)                        NOT NULL                            UNIQUE,
    password                VARCHAR(100)                        NOT NULL,
    is_active               BOOLEAN                             NOT NULL                            DEFAULT TRUE,
    CONSTRAINT              users_pk                            PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS trainees (
    id                      SERIAL                              NOT NULL                            UNIQUE,
    date_of_birth           DATE,
    address                 VARCHAR(200),
    CONSTRAINT              trainees_pk                         PRIMARY KEY (id),
    CONSTRAINT              trainees_users_fk                   FOREIGN KEY (id)                    REFERENCES users(id)                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainers (
    id                      SERIAL                              NOT NULL                            UNIQUE,
    training_type_id        BIGINT                              NOT NULL,
    CONSTRAINT              trainers_pk                         PRIMARY KEY (id),
    CONSTRAINT              trainers_users_fk                   FOREIGN KEY (id)                    REFERENCES users(id)                ON DELETE CASCADE,
    CONSTRAINT              trainers_training_types_fk          FOREIGN KEY (training_type_id)      REFERENCES training_types(id)       ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainings (
    id                      SERIAL                              NOT NULL                            UNIQUE,
    trainee_id              BIGINT                              NOT NULL,
    trainer_id              BIGINT                              NOT NULL,
    training_name           VARCHAR(100)                        NOT NULL,
    training_type_id        BIGINT                              NOT NULL,
    training_date           DATE                                NOT NULL,
    training_duration       BIGINT                              NOT NULL,
    CONSTRAINT              trainings_pk                        PRIMARY KEY (id),
    CONSTRAINT              trainings_trainers_fk               FOREIGN KEY (trainer_id)            REFERENCES trainers(id)             ON DELETE CASCADE,
    CONSTRAINT              trainings_trainees_fk               FOREIGN KEY (trainee_id)            REFERENCES trainees(id)             ON DELETE CASCADE,
    CONSTRAINT              trainings_training_types_fk         FOREIGN KEY (training_type_id)      REFERENCES training_types(id)       ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainees_to_trainers (
    id                     SERIAL                              NOT NULL                            UNIQUE,
    trainee_id             BIGINT                              NOT NULL,
    trainer_id             BIGINT                              NOT NULL,
    CONSTRAINT             trainees_to_trainers_pk             PRIMARY KEY (id),
    CONSTRAINT             trainees_to_trainers_trainees_fk    FOREIGN KEY (trainee_id)            REFERENCES trainees(id)              ON DELETE CASCADE,
    CONSTRAINT             trainees_to_trainers_trainers_fk    FOREIGN KEY (trainer_id)            REFERENCES trainers(id)              ON DELETE CASCADE
);