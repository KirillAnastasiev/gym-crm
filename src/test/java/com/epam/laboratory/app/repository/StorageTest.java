package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.domain.Trainee;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.domain.TrainingType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageTest {
    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage.getStorageMap().clear();
    }

    @Test
    @DisplayName("Test of the method update - should update entity in storage map")
    void testUpdate() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        trainee.setFirstName("UpdatedFirstName");

        // when
        storage.update(trainee);

        // then
        assertThat(storageMap).isNotEmpty();
        assertThat(storageMap).containsKey("trainee:1");
        assertThat(storageMap).containsValue(trainee);
        assertThat(storageMap.get("trainee:1")).isSameAs(trainee);
    }

    @Test
    @DisplayName("Test of the method remove - should remove entity from storage map by key")
    void testRemove() {
        // given
        var storageMap = storage.getStorageMap();
        var trainee = createTestTrainee();
        storageMap.put("trainee:1", trainee);

        // when
        storage.remove(trainee);

        // then
        assertThat(storageMap).doesNotContainKey("trainee:1");
        assertThat(storageMap).doesNotContainValue(trainee);
    }

    @Test
    @DisplayName("Test of the method afterPropertiesSet - should read storage map from file and put it to storage map")
    @SuppressWarnings("unchecked")
    void testAfterPropertiesSet() throws Exception {
        // given
        var storageFilePath = "src/test/resources/test_storage.json";
        storage.setStorageFilePath(storageFilePath);
        var testMap = createTestStorageMapWithLinkedHashMap();

        given(jsonMapper.readValue(anyString(), any(TypeReference.class))).willReturn(testMap);
        given(jsonMapper.convertValue(any(LinkedHashMap.class), eq(Trainee.class))).willAnswer(invocation -> createTestTrainee());
        given(jsonMapper.convertValue(any(LinkedHashMap.class), eq(Trainer.class))).willAnswer(invocation -> createTestTrainer());
        given(jsonMapper.convertValue(any(LinkedHashMap.class), eq(Training.class))).willAnswer(invocation -> createTestTraining());

        // when
        storage.afterPropertiesSet();

        // then
        assertThat(storage.getStorageMap()).hasSize(3);
        assertThat(storage.getStorageMap()).containsKeys("trainee:1", "trainer:1", "training:1");
        assertThat(storage.getStorageMap().get("trainee:1")).isInstanceOf(Trainee.class);
        assertThat(storage.getStorageMap().get("trainer:1")).isInstanceOf(Trainer.class);
        assertThat(storage.getStorageMap().get("training:1")).isInstanceOf(Training.class);

        verify(jsonMapper, times(1)).readValue(anyString(), any(TypeReference.class));
        verify(jsonMapper, times(1)).convertValue(any(LinkedHashMap.class), eq(Trainee.class));
        verify(jsonMapper, times(1)).convertValue(any(LinkedHashMap.class), eq(Trainer.class));
        verify(jsonMapper, times(1)).convertValue(any(LinkedHashMap.class), eq(Training.class));
    }

    @Test
    @DisplayName("Test of the method destroy - should write storage map to file in json format")
    void testDestroy() throws Exception {
        // given
        var storageFilePath = "src/test/resources/test_storage.json";
        storage.setStorageFilePath(storageFilePath);
        var testJsonString = createTestJsonString();

        given(jsonMapper.writeValueAsString(any(Map.class))).willReturn(testJsonString);

        // when
        storage.destroy();

        // then
        String actualJson = Files.readString(Path.of(storageFilePath));
        assertThat(actualJson).isEqualTo(testJsonString);

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

