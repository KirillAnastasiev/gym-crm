package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainees", schema = "public")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = true)
@ToString(callSuper = true, exclude = {"password", "trainings", "trainers"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"dateOfBirth", "address"})
public class Trainee extends User {

    @Column(name = "date_of_birth")
    @JsonProperty(value = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 200)
    @JsonProperty(value = "address")
    private String address;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "trainee")
    @Setter(AccessLevel.PRIVATE)
    private Collection<Training> trainings = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "trainees_to_trainers", joinColumns = @JoinColumn(name = "trainee_id"), inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    @Setter(AccessLevel.PRIVATE)
    private Collection<Trainer> trainers = new HashSet<>();

    public void addTraining(Training training) {
        trainings.add(training);
        training.setTrainee(this);
    }

    public void addTrainings(Collection<Training> trainings) {
        this.trainings.addAll(trainings);
        trainings.forEach(training -> training.setTrainee(this));
    }

    public void removeTraining(Training training) {
        trainings.remove(training);
        training.setTrainee(null);
    }

    public Collection<Training> getTrainings() {
        return Set.copyOf(trainings);
    }

    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
        if (!trainer.getTrainees().contains(this)) {
            trainer.addTrainee(this);
        }
    }

    public void addTrainers(Collection<Trainer> trainers) {
        this.trainers.addAll(trainers);
        trainers.forEach(trainer -> {
            if (!trainer.getTrainees().contains(this)) {
                trainer.addTrainee(this);
            }
        });
    }

    public void removeTrainer(Trainer trainer) {
        trainers.remove(trainer);
        if  (trainer.getTrainees().contains(this)) {
            trainer.removeTrainee(this);
        }
    }

     public Collection<Trainer> getTrainers() {
        return Set.copyOf(trainers);
    }
}
