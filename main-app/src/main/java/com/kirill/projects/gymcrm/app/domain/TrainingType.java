package com.kirill.projects.gymcrm.app.domain;

import jakarta.persistence.*;
import lombok.*;

@jakarta.persistence.Entity
@Table(name = "training_types")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class TrainingType implements Entity {
    public static final String FITNESS = "Fitness";
    public static final String YOGA = "Yoga";
    public static final String ZUMBA = "Zumba";
    public static final String STRETCHING = "Stretching";
    public static final String CROSSFIT = "Crossfit";
    public static final String PILATES = "Pilates";
    public static final String CARDIO = "Cardio";
    public static final String RESISTANCE = "Resistance";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "training_types_id_seq_gen")
    @SequenceGenerator(name = "training_types_id_seq_gen", sequenceName = "training_types_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "training_type_name", nullable = false, updatable = false, unique = true, length = 50)
    private String trainingTypeName;

}
