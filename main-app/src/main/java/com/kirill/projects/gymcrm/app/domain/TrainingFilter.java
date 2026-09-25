package com.kirill.projects.gymcrm.app.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TrainingFilter {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingTypeName;
    private LocalDateTime periodFrom;
    private LocalDateTime periodTo;
}
