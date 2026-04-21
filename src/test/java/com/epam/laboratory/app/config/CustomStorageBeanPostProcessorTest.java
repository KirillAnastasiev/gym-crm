package com.epam.laboratory.app.config;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.epam.laboratory.app.repository.Storage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomStorageBeanPostProcessorTest {

    private final String storageFilePath = "src/test/resources/test_storage.json";

    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private CustomStorageBeanPostProcessor customStorageBeanPostProcessor;

    @BeforeEach
    void setUp() {
        customStorageBeanPostProcessor.setStorageFilePath(storageFilePath);
    }

    @Test
    @DisplayName("Test of the method postProcessBeforeInitialization - should read json from file and convert it to storage map")
    void testPostProcessBeforeInitialization() throws Exception {
       // given
        var storageMap = createTestStorageMapWithLinkedHashMap();

        given(jsonMapper.readValue(anyString(), any(TypeReference.class))).willReturn(storageMap);
        given(jsonMapper.convertValue(any(LinkedHashMap.class), eq(Trainee.class))).willAnswer(invocation -> createTestTrainee());
        given(jsonMapper.convertValue(any(LinkedHashMap.class), eq(Trainer.class))).willAnswer(invocation -> createTestTrainer());
        given(jsonMapper.convertValue(any(LinkedHashMap.class), eq(Training.class))).willAnswer(invocation -> createTestTraining());


        // when
        var result = customStorageBeanPostProcessor.postProcessBeforeInitialization(new Storage(jsonMapper), "storage");

        // then
        assertThat(result).isInstanceOf(Storage.class);
        var storage = (Storage) result;
        assertThat(storage.getStorageMap()).isNotEmpty();
        assertThat(storage.getStorageMap()).containsKey("trainee:1");
        assertThat(storage.getStorageMap()).containsKey("trainer:1");
        assertThat(storage.getStorageMap()).containsKey("training:1");
        assertThat(storage.getStorageMap().get("trainee:1")).isInstanceOf(Trainee.class);
        assertThat(storage.getStorageMap().get("trainer:1")).isInstanceOf(Trainer.class);
        assertThat(storage.getStorageMap().get("training:1")).isInstanceOf(Training.class);

        verify(jsonMapper, times(1)).readValue(anyString(), any(TypeReference.class));
        verify(jsonMapper, times(1)).convertValue(any(LinkedHashMap.class), eq(Trainee.class));
        verify(jsonMapper, times(1)).convertValue(any(LinkedHashMap.class), eq(Trainer.class));
        verify(jsonMapper, times(1)).convertValue(any(LinkedHashMap.class), eq(Training.class));
    }

    @Test
    @DisplayName("Test of the method postProcessBeforeDestruction - should convert storage map to json and write it to file")
    void testPostProcessBeforeDestruction() throws Exception {
        // given
        var storage = new Storage(jsonMapper);
        var jsonString = createTestJsonString();

        given(jsonMapper.writeValueAsString(any(Map.class))).willReturn(jsonString);

        // when
        customStorageBeanPostProcessor.postProcessBeforeDestruction(storage, "storage");

        // then
        verify(jsonMapper, times(1)).writeValueAsString(any(Map.class));
    }

    private Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");
        trainee.setPassword("0123456789");
        trainee.setAddress("Test Address");
        trainee.setDateOfBirth(LocalDate.now());
        trainee.setActive(true);

        return trainee;
    }

    private Trainer createTestTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("FirstName");
        trainer.setLastName("LastName");
        trainer.setUsername("FirstName.LastName");
        trainer.setPassword("9876543210");
        trainer.setActive(false);
        trainer.setSpecialization(TrainingType.FITNESS);

        return trainer;
    }

    private Training createTestTraining() {
        var training = new Training();
        training.setId(1L);
        training.setTrainee(createTestTrainee());
        training.setTrainer(createTestTrainer());
        training.setTrainingName("Test Training");
        training.setTrainingType(TrainingType.FITNESS);
        training.setTrainingDate(LocalDate.now().atStartOfDay());
        training.setTrainingDuration(java.time.Duration.ofHours(1));

        return training;
    }

    private Map<String, LinkedHashMap<String, Object>> createTestStorageMapWithLinkedHashMap() {
        Map<String, LinkedHashMap<String, Object>> map = new LinkedHashMap<>();
        map.put("trainee:1", new LinkedHashMap<>());
        map.put("trainer:1", new LinkedHashMap<>());
        map.put("training:1", new LinkedHashMap<>());
        return map;
    }

    private String createTestJsonString() {
        return """
                {
                  "trainee:1" : {
                    "dateOfBirth" : [ 2026, 4, 17 ],
                    "address" : "Test Address",
                    "active" : false,
                    "id" : 1,
                    "firstName" : "FirstName",
                    "lastName" : "LastName",
                    "username" : "FirstName.LastName",
                    "password" : "0123456789"
                  },
                  "trainer:1" : {
                    "specialization" : "FITNESS",
                    "active" : false,
                    "id" : 1,
                    "firstName" : "FirstName",
                    "lastName" : "LastName",
                    "username" : "FirstName.LastName",
                    "password" : "9876543210"
                  },
                  "training:1" : {
                    "id" : 1,
                    "trainee" : {
                      "dateOfBirth" : [ 2026, 4, 17 ],
                      "address" : "Test Address",
                      "active" : false,
                      "id" : 1,
                      "firstName" : "FirstName",
                      "lastName" : "LastName",
                      "username" : "FirstName.LastName",
                      "password" : "0123456789"
                    },
                    "trainer" : {
                      "specialization" : "FITNESS",
                      "active" : false,
                      "id" : 1,
                      "firstName" : "FirstName",
                      "lastName" : "LastName",
                      "username" : "FirstName.LastName",
                      "password" : "9876543210"
                    },
                    "trainingName" : "Test Training",
                    "trainingType" : "FITNESS",
                    "trainingDate" : [ 2026, 4, 17, 0, 0 ],
                    "trainingDuration" : 3600.000000000
                  }
                }""";
    }
}