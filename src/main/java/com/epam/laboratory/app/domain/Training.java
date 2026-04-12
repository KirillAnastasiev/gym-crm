package com.epam.laboratory.app.domain;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Training {
    private Long id;

    private Trainee trainee;

    private Trainer trainer;

    private String trainingName;

    private TrainingType trainingType;

    private LocalDateTime trainingDate;

    private Duration trainingDuration;
}
