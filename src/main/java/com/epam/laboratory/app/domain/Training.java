package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "trainings", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainee", "trainer", "trainingName", "trainingType", "trainingDate", "trainingDuration"})
public class Training implements  Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @JsonProperty(value = "id", required = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "trainee_id", nullable = false)
    @JsonProperty(value = "trainee", required = true)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "trainer_id", nullable = false)
    @JsonProperty(value = "trainer", required = true)
    private Trainer trainer;

    @Column(name = "training_name", nullable = false, length = 100)
    @JsonProperty(value = "trainingName", required = true)
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    @JsonProperty(value = "trainingType", required = true)
    private TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    @JsonProperty(value = "trainingDate", required = true)
    private LocalDateTime trainingDate;

    @Column(name = "training_duration", nullable = false)
    @JsonProperty(value = "trainingDuration", required = true)
    private Duration trainingDuration;

}
