package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "training_types", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Service
@EqualsAndHashCode(of = "id")
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainingTypeName"})
public class TrainingType implements BaseEntity {
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
    @JsonProperty(value = "id", required = true)
    private Long id;

    @Column(name = "training_type_name", nullable = false, updatable = false, unique = true, length = 50)
    @JsonProperty(value = "trainingTypeName", required = true)
    private String trainingTypeName;

}
