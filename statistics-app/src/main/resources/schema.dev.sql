CREATE SCHEMA IF NOT EXISTS public;
SET SCHEMA public;

DROP SEQUENCE IF EXISTS trainings_id_seq;

DROP TABLE IF EXISTS trainings;

CREATE SEQUENCE IF NOT EXISTS trainings_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS trainings (
    id                          BIGINT                      NOT NULL,
    trainer_username            VARCHAR(110)                NOT NULL,
    trainer_first_name          VARCHAR(50)                 NOT NULL,
    trainer_last_name           VARCHAR(50)                 NOT NULL,
    trainer_status              VARCHAR(10)                 NOT NULL,
    training_date               TIMESTAMP                   NOT NULL,
    training_duration           BIGINT                      NOT NULL,
    CONSTRAINT                  trainings_pk                PRIMARY KEY (id)
);