package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers", schema = "public")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = true)
@ToString(callSuper = true, exclude = {"password", "trainings", "trainees"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"specialization"})
public class Trainer extends User {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "training_type_id", nullable = false)
    @JsonProperty(value = "specialization", required = true)
    private TrainingType specialization;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "trainer")
    @Setter(AccessLevel.PRIVATE)
    private Collection<Training> trainings = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "trainers")
    @Setter(AccessLevel.PRIVATE)
    private Collection<Trainee> trainees = new HashSet<>();

    public void addTraining(Training training) {
        trainings.add(training);
        training.setTrainer(this);
    }

    public void addTrainings(Collection<Training> trainings) {
        this.trainings.addAll(trainings);
        trainings.forEach(training -> training.setTrainer(this));
    }

    public void removeTraining(Training training) {
        if (trainings.contains(training)) {
            trainings.remove(training);
            training.setTrainer(null);
        }
    }

    public Collection<Training> getTrainings() {
        return Set.copyOf(trainings);
    }

    public void addTrainee(Trainee trainee) {
        trainees.add(trainee);
        if (!trainee.getTrainers().contains(this)) {
            trainee.addTrainer(this);
        }
    }

    public void addTrainees(Collection<Trainee> trainees) {
        this.trainees.addAll(trainees);
        trainees.forEach(trainee -> {
            if (!trainee.getTrainers().contains(this)) {
                trainee.addTrainer(this);
            }
        });
    }

    public void removeTrainee(Trainee trainee) {
        if (trainees.contains(trainee)) {
            trainees.remove(trainee);
            if (trainee.getTrainers().contains(this)) {
                trainee.removeTrainer(this);
            }
        }
    }

    public Collection<Trainee> getTrainees() {
        return Set.copyOf(trainees);
    }

}
