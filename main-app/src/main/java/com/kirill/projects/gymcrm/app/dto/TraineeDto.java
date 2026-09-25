package com.kirill.projects.gymcrm.app.dto;

import com.kirill.projects.gymcrm.app.dto.annotation.Required;
import com.kirill.projects.gymcrm.app.dto.annotation.Sensitive;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Collection;

import static io.swagger.v3.oas.annotations.media.Schema.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "firstName", "lastName", "username", "password", "dateOfBirth", "address", "trainers", "active"})
@Schema(description = "Trainee in the Gym CRM system")
public record TraineeDto(
        @JsonProperty("id")
        @Schema(
                description = "Unique identifier of the trainee",
                example = "1"
        )
        Long id,

        @Required
        @JsonProperty("firstName")
        @Schema(
                description = "First name of the trainee",
                example = "John",
                requiredMode = RequiredMode.REQUIRED
        )
        String firstName,

        @Required
        @JsonProperty("lastName")
        @Schema(
                description = "Last name of the trainee",
                example = "Doe",
                requiredMode = RequiredMode.REQUIRED
        )
        String lastName,

        @JsonProperty("username")
        @Schema(
                description = "Username for trainee login",
                example = "John.Doe"
        )
        String username,

        @JsonProperty("password")
        @Sensitive
        @Schema(
                description = "Password for trainee login",
                example = "password123"
        )
        String password,

        @JsonProperty("dateOfBirth")
        @Schema(
                description = "Date of birth of the trainee in ISO format (YYYY-MM-DD)",
                example = "1990-01-01"
        )
        LocalDate dateOfBirth,

        @JsonProperty("address")
        @Schema(
                description = "Address of the trainee",
                example = "123 Main St"
        )
        String address,

        @JsonProperty("trainers")
        @ArraySchema(schema = @Schema(
                description = "List of trainers assigned to the trainee",
                example = """
                        [
                                {
                                        "id": 5,
                                        "firstName": "Sarah",
                                        "lastName": "Davis",
                                        "username": "Sarah.Davis",
                                        "password": "password654",
                                        "specialization" : {
                                                "id" : 1,
                                                "trainingTypeName" : "Fitness"
                                        },
                                        "active": true
                                }, {
                                        "id": 7,
                                        "firstName": "Laura",
                                        "lastName": "Miller",
                                        "username": "Laura.Miller",
                                        "password": "password111",
                                        "specialization" : {
                                                "id" : 3,
                                                "trainingTypeName" : "Zumba"
                                        },
                                        "active": true
                                }
                        ]"""
                )
        )
        Collection<TrainerDto> trainers,

        @JsonProperty("active")
        @Schema(
                description = "Indicates whether the trainee is active",
                example = "true"
        )
        Boolean active
) {
}
