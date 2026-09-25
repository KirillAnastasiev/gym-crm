package com.kirill.projects.gymcrm.app.repository;

import com.kirill.projects.gymcrm.app.config.MongoDBConfig;
import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTest
@Import({
        MongoDBConfig.class
})
@DisplayName("TrainingStatisticsDao test suite")
class TrainingStatisticsDaoTest {

    @Autowired
    private TrainingStatisticsDao trainingStatisticsDao;

    @BeforeEach
    void setUp() {
        trainingStatisticsDao.deleteAll();
        var statistics = createTestTrainingStatistics();
        trainingStatisticsDao.save(statistics);
    }

    // ==================== FIND BY TRAINER USERNAME TESTS ====================

    @Test
    @DisplayName("Test of the method findByTrainerUsername - should return statistics when trainer username exists")
    void testFindByTrainerUsername_positive() {
        // given
        var statistics = createTestTrainingStatistics();
        var username = "Sarah.Davis";

        // when
        var actualResult = trainingStatisticsDao.findByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.map(TrainingStatistics::getId)).isNotEmpty();
        assertThat(actualResult.map(TrainingStatistics::getTrainerUsername)).hasValue(username);
        assertThat(actualResult.get()).usingRecursiveComparison().ignoringFields("id").isEqualTo(statistics);
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsername - should return empty when trainer username does not exist")
    void testFindByTrainerUsername_negative_notExistedUser() {
        // given
        var username = "Unknown.Username";

        // when
        var actualResult = trainingStatisticsDao.findByTrainerUsername(username);

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }


    // ==================== FIND BY TRAINER USERNAME BETWEEN DATES TESTS ====================

    @Test
    @DisplayName("Test of the method findByTrainerUsernameBetweenDates - should return statistics when trainer username exists and dates are correct")
    void testFindByTrainerUsernameBetweenDates_positive() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2018, Month.APRIL, 1);
        var dateTo = LocalDate.of(2026, Month.APRIL, 2);

        // when
        var actualResult = trainingStatisticsDao.findByTrainerUsernameBetweenDates(username, dateFrom.getYear(), dateFrom.getMonthValue(), dateTo.getYear(), dateTo.getMonthValue());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.map(TrainingStatistics::getId)).isNotEmpty();
        assertThat(actualResult.map(TrainingStatistics::getTrainerUsername)).hasValue(username);
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsernameBetweenDates - should return empty when trainer username does not exist")
    void testFindByTrainerUsernameBetweenDates_negative_notExistedUser() {
        // given
        var username = "Unknown.Username";
        var dateFrom = LocalDate.of(2018, Month.APRIL, 1);
        var dateTo = LocalDate.of(2026, Month.APRIL, 2);

        // when
        var actualResult = trainingStatisticsDao.findByTrainerUsernameBetweenDates(username, dateFrom.getYear(), dateFrom.getMonthValue(), dateTo.getYear(), dateTo.getMonthValue());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("Test of the method findByTrainerUsernameBetweenDates - should return empty when trainer username exists but dates are not satisfied")
    void testFindByTrainerUsernameBetweenDates_negative_unsatisfiedDates() {
        // given
        var username = "Sarah.Davis";
        var dateFrom = LocalDate.of(2018, Month.APRIL, 1);
        var dateTo = LocalDate.of(2020, Month.APRIL, 2);

        // when
        var actualResult = trainingStatisticsDao.findByTrainerUsernameBetweenDates(username, dateFrom.getYear(), dateFrom.getMonthValue(), dateTo.getYear(), dateTo.getMonthValue());

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }


    private static TrainingStatistics createTestTrainingStatistics() {
        var statistics = new TrainingStatistics();
        statistics.setTrainerUsername("Sarah.Davis");
        statistics.setTrainerFirstName("Sarah");
        statistics.setTrainerLastName("Davis");
        statistics.setTrainerStatus(true);

        var yearStatistics = new TrainingStatistics.YearStatistics();
        yearStatistics.setYear(Year.of(2024));

        var monthStatistics = new TrainingStatistics.MonthStatistics();
        monthStatistics.setMonth(Month.JULY);
        monthStatistics.setTotalDuration(Duration.ofMillis(32400000));
        var monthStatisticsSet = new HashSet<TrainingStatistics.MonthStatistics>();
        monthStatisticsSet.add(monthStatistics);
        yearStatistics.setMonthStatistics(monthStatisticsSet);
        var yearStatisticSet = new HashSet<TrainingStatistics.YearStatistics>();
        yearStatisticSet.add(yearStatistics);
        statistics.setYearStatisticsSet(yearStatisticSet);
        return statistics;
    }

}