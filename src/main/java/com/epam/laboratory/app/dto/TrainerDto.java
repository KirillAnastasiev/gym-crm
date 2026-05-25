package com.epam.laboratory.app.dto;

import com.epam.laboratory.app.dto.annotation.Required;
import com.epam.laboratory.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.OptBoolean;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Description;

import java.util.Collection;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "specialization", "trainees", "active"})
@Schema(description = "Trainer in the Gym CRM system")
public record TrainerDto(
        @JsonProperty("id")
        @Schema(
                description = "Unique identifier of the trainer",
                example = "5"
        )
        Long id,

        @Required
        @JsonProperty("firstName")
        @Schema(
                description = "First name of the trainer",
                example = "Sarah",
                requiredMode = RequiredMode.REQUIRED
        )
        String firstName,

        @Required
        @JsonProperty("lastName")
        @Schema(
                description = "Last name of the trainer",
                example = "Davis",
                requiredMode = RequiredMode.REQUIRED
        )
        String lastName,

        @JsonProperty("username")
        @Schema(
                description = "Username for trainer login",
                example = "Sarah.Davis"
        )
        String username,

        @JsonProperty("password")
        @Sensitive
        @Schema(
                description = "Password for trainer login",
                example = "password654"
        )
        String password,

        @Required
        @JsonProperty("specialization")
        @Schema(
                description = "Trainer's specialization",
                example = """
                        {
                            "id": 1,
                            "trainingTypeName": "Fitness"
                        }""",
                requiredMode = RequiredMode.REQUIRED
        )
        TrainingTypeDto specialization,

        @JsonProperty("trainees")
        @ArraySchema(schema = @Schema(
                description = "List of trainees assigned to the trainer",
                example = """
                        [
                                {
                                        "id": 1,
                                        "firstName": "John",
                                        "lastName": "Doe",
                                        "username": "John.Doe",
                                        "password": "password123",
                                        "dateOfBirth": "1990-01-01",
                                        "address": "123 Main St",
                                        "active": true
                                }, {
                                        "id": 2,
                                        "firstName": "Jane",
                                        "lastName": "Smith",
                                        "username": "Jane.Smith",
                                        "password": "password456",
                                        "dateOfBirth": "1985-05-15",
                                        "address": "456 Elm St",
                                        "active": true
                                }
                        ]"""
        ))
        Collection<TraineeDto> trainees,

        @JsonProperty("active")
        @Schema(
                description = "Indicates whether the trainer is active",
                example = "true"
        )
        Boolean active
) {
}
