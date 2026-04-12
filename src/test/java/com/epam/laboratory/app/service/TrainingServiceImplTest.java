package com.epam.laboratory.app.service;

import com.epam.laboratory.app.dao.TrainingDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingServiceImpl;

    @Test
    void testCreteTrainingService() {
    }

    @Test
    void testSelectTraining() {
    }
}