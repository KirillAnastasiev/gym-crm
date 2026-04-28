package com.epam.laboratory.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

@jakarta.persistence.Entity
@Table(name = "training_types", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Service
@EqualsAndHashCode(of = "id")
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "trainingTypeName"})
public class TrainingType implements Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @JsonProperty(value = "id", required = true)
    private Long id;

    @Column(name = "training_type_name", nullable = false, updatable = false, unique = true, length = 50)
    @JsonProperty(value = "trainingTypeName", required = true)
    private String trainingTypeName;

}
