package com.kirill.projects.gymcrm.app.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "training_statistics")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TrainingStatistics {
    @Id
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "trainer_username", targetType = FieldType.STRING)
    private String trainerUsername;

    @Field(name = "trainer_firstname", targetType = FieldType.STRING)
    private String trainerFirstName;

    @Field(name = "trainer_lastname", targetType = FieldType.STRING)
    private String trainerLastName;

    @Field(name = "trainer_status", targetType = FieldType.BOOLEAN)
    private Boolean trainerStatus;

    @Field(name = "years_list", targetType = FieldType.ARRAY)
    private HashSet<YearStatistics> yearStatisticsSet;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class YearStatistics {
        @Field(name = "year", targetType = FieldType.INT64)
        private Year year;

        @Field(name = "months_list", targetType = FieldType.ARRAY)
        private HashSet<MonthStatistics> monthStatistics;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class MonthStatistics {
        @Field(name = "month", targetType = FieldType.INT32)
        private Month month;

        @Field(name = "trainings_duration", targetType = FieldType.INT32)
        private Duration totalDuration;
    }
}
