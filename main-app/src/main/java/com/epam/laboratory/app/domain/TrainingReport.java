package com.epam.laboratory.app.domain;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TrainingReport {

    private String trainerUsername;

    private String trainerFirsName;

    private String trainerLastName;

    private Boolean isActive;

    private LocalDateTime trainingDate;

    private Duration trainingDuration;

    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }

}
