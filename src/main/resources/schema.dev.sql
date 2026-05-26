DROP TABLE IF EXISTS jwt_tokens;
DROP TABLE IF EXISTS trainees_to_trainers;
DROP TABLE IF EXISTS trainings;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS trainees;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS training_types;

DROP SEQUENCE IF EXISTS training_types_id_seq;
DROP SEQUENCE IF EXISTS users_id_seq;
DROP SEQUENCE IF EXISTS trainees_id_seq;
DROP SEQUENCE IF EXISTS trainers_id_seq;
DROP SEQUENCE IF EXISTS trainings_id_seq;
DROP SEQUENCE IF EXISTS trainees_to_trainers_id_seq;

CREATE SEQUENCE training_types_id_seq START WITH 1;
CREATE SEQUENCE users_id_seq START WITH 1;
CREATE SEQUENCE trainees_id_seq START WITH 1;
CREATE SEQUENCE trainers_id_seq START WITH 1;
CREATE SEQUENCE trainings_id_seq START WITH 1;
CREATE SEQUENCE trainees_to_trainers_id_seq START WITH 1;

CREATE TABLE IF NOT EXISTS training_types (
    id                      BIGINT                              NOT NULL                            DEFAULT NEXTVAL('training_types_id_seq'),
    training_type_name      VARCHAR(50)                         NOT NULL                            UNIQUE,
    CONSTRAINT              training_types_pk                   PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id                      BIGINT                              NOT NULL                            DEFAULT NEXTVAL('users_id_seq'),
    first_name              VARCHAR(50)                         NOT NULL,
    last_name               VARCHAR(50)                         NOT NULL,
    username                VARCHAR(110)                        NOT NULL                            UNIQUE,
    password                VARCHAR(100)                        NOT NULL,
    is_active               BOOLEAN                             NOT NULL                            DEFAULT TRUE,
    CONSTRAINT              users_pk                            PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS trainees (
    id                      BIGINT                              NOT NULL                            DEFAULT NEXTVAL('trainees_id_seq'),
    date_of_birth           DATE,
    address                 VARCHAR(200),
    CONSTRAINT              trainees_pk                         PRIMARY KEY (id),
    CONSTRAINT              trainees_users_fk                   FOREIGN KEY (id)                    REFERENCES users(id)                ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainers (
    id                      BIGINT                              NOT NULL                            DEFAULT NEXTVAL('trainers_id_seq'),
    training_type_id        BIGINT                              NOT NULL,
    CONSTRAINT              trainers_pk                         PRIMARY KEY (id),
    CONSTRAINT              trainers_users_fk                   FOREIGN KEY (id)                    REFERENCES users(id)                ON DELETE CASCADE,
    CONSTRAINT              trainers_training_types_fk          FOREIGN KEY (training_type_id)      REFERENCES training_types(id)       ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainings (
    id                      BIGINT                              NOT NULL                            DEFAULT NEXTVAL('trainings_id_seq'),
    trainee_id              BIGINT                              NOT NULL,
    trainer_id              BIGINT                              NOT NULL,
    training_name           VARCHAR(100)                        NOT NULL,
    training_type_id        BIGINT                              NOT NULL,
    training_date           TIMESTAMP                           NOT NULL,
    training_duration       BIGINT                              NOT NULL,
    CONSTRAINT              trainings_pk                        PRIMARY KEY (id),
    CONSTRAINT              trainings_trainers_fk               FOREIGN KEY (trainer_id)            REFERENCES trainers(id)             ON DELETE CASCADE,
    CONSTRAINT              trainings_trainees_fk               FOREIGN KEY (trainee_id)            REFERENCES trainees(id)             ON DELETE CASCADE,
    CONSTRAINT              trainings_training_types_fk         FOREIGN KEY (training_type_id)      REFERENCES training_types(id)       ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trainees_to_trainers (
    id                     BIGINT                              NOT NULL                             DEFAULT NEXTVAL('trainees_to_trainers_id_seq'),
    trainee_id             BIGINT                              NOT NULL,
    trainer_id             BIGINT                              NOT NULL,
    CONSTRAINT             trainees_to_trainers_pk             PRIMARY KEY (id),
    CONSTRAINT             trainees_to_trainers_trainees_fk    FOREIGN KEY (trainee_id)             REFERENCES trainees(id)              ON DELETE CASCADE,
    CONSTRAINT             trainees_to_trainers_trainers_fk    FOREIGN KEY (trainer_id)             REFERENCES trainers(id)              ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jwt_tokens (
    id                      UUID                                NOT NULL                            DEFAULT GEN_RANDOM_UUID(),
    token_type              VARCHAR(20)                         NOT NULL,
    expiry_date             TIMESTAMP                           NOT NULL,
    username                VARCHAR(110)                        NOT NULL,
    revoked                 BOOLEAN                             NOT NULL                            DEFAULT FALSE,
    CONSTRAINT              jwt_tokens_pk                       PRIMARY KEY (id),
    CONSTRAINT              jwt_tokens_users_fk                 FOREIGN KEY (username)              REFERENCES users(username)           ON DELETE CASCADE
);