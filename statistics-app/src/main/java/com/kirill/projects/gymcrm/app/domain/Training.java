package com.kirill.projects.gymcrm.app.domain;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Training {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private TrainerStatus trainerStatus;
    private LocalDateTime trainingDate;
    private Duration trainingDuration;
}
