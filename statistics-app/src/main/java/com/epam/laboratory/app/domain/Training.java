package com.epam.laboratory.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "trainings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "trainings_id_seq", sequenceName = "trainings_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "trainer_username", nullable = false)
    private String trainerUsername;

    @Column(name = "trainer_first_name", nullable = false)
    private String trainerFirstName;

    @Column(name = "trainer_last_name", nullable = false)
    private String trainerLastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "trainer_status", nullable = false)
    private TrainerStatus trainerStatus;

    @Column(name = "training_date", nullable = false)
    private LocalDateTime trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Duration trainingDuration;

}
