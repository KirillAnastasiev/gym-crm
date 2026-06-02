package com.epam.laboratory.app.domain;

import lombok.*;

import java.time.Duration;
import java.time.Month;
import java.time.Year;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TrainingStatistics {
    private String trainerUsername;
    private Map<Year, Map<Month, Duration>> trainingSummary;
}
