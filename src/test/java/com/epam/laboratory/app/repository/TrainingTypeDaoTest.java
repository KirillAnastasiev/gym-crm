package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Sql(scripts = {
        "classpath:schema.test.sql",
        "classpath:data.test.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("TrainingTypeDao test suite")
class TrainingTypeDaoTest {

    @Autowired
    private TrainingTypeDao trainingTypeDao;


    // ==================== FIND ALL TESTS ====================

    @Test
    @DisplayName("Test of the method findAll - should return list of all training types")
    void testFindAll() {
        // given
        var trainingTypes = createTestTrainingTypes();

        // when
        var actualResult = trainingTypeDao.findAll();

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).containsAll(trainingTypes);
    }


    // ==================== FIND BY TRAINING TYPE NAME TESTS ====================

    @Test
    @DisplayName("Test of the method findByTrainingTypeName - should return training type with given name")
    void testFindByTrainingTypeName() {
        // given
        var trainingTypeName = "Yoga";

        // when
        var actualResult = trainingTypeDao.findByTrainingTypeName(trainingTypeName);

        // then
        assertThat(actualResult).isPresent();
        assertThat(actualResult.map(TrainingType::getTrainingTypeName)).contains(trainingTypeName);
    }


    // ==================== COUNT TESTS ====================

    @Test
    @DisplayName("Test of the method count - should return total number of training types")
    void testCount() {
        // when
        var actualResult = trainingTypeDao.count();

        // then
        assertThat(actualResult).isEqualTo(8L);
    }

    private static List<TrainingType> createTestTrainingTypes() {
        var trainingType1 = new TrainingType();
        trainingType1.setId(1L);
        trainingType1.setTrainingTypeName("Fitness");

        var trainingType2 = new TrainingType();
        trainingType2.setId(2L);
        trainingType2.setTrainingTypeName("Yoga");

        var trainingType3 = new TrainingType();
        trainingType3.setId(3L);
        trainingType3.setTrainingTypeName("Zumba");

        var trainingType4 = new TrainingType();
        trainingType4.setId(4L);
        trainingType4.setTrainingTypeName("Stretching");

        var trainingType5 = new TrainingType();
        trainingType5.setId(5L);
        trainingType5.setTrainingTypeName("Crossfit");

        var trainingType6 = new TrainingType();
        trainingType6.setId(6L);
        trainingType6.setTrainingTypeName("Pilates");

        var trainingType7 = new TrainingType();
        trainingType7.setId(7L);
        trainingType7.setTrainingTypeName("Cardio");

        return List.of(trainingType1, trainingType2, trainingType3, trainingType4, trainingType5, trainingType6, trainingType7);
    }

}