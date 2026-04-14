package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class StorageTest {
    private Storage storage;

    @BeforeEach
    public void setUp() {
        storage = new Storage();
    }

    @Test
    @DisplayName("Test of the method put - should put entity to storage map")
    public void testPut() {
        // given
        var storageMap = storage.getStorage();
        var trainee = createTestTrainee();

        // when
        storage.put("trainee:1", trainee);

        // then
        assertThat(storageMap).isNotEmpty();
        assertThat(storageMap).containsKey("trainee:1");
        assertThat(storageMap).containsValue(trainee);
        assertThat(storageMap.get("trainee:1")).isSameAs(trainee);
    }

    @Test
    @DisplayName("Test of the method get - should return entity from storage map by key")
    public void testGet_positive() {
        // given
        var storageMap = storage.getStorage();
        var trainee = createTestTrainee();
        storageMap.put("trainee:1", trainee);

        // when
        var actualResult = storage.get("trainee:1");

        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isSameAs(trainee);
    }

    @Test
    @DisplayName("Test of the method get - should return null if key is not present in storage map")
    public void testGet_negative() {
        // when
        var actualResult = storage.get("trainee:1");

        // then
        assertThat(actualResult).isNull();
    }

    @Test
    @DisplayName("Test of the method remove - should remove entity from storage map by key")
    public void testRemove() {
        // given
        var storageMap = storage.getStorage();
        var trainee = createTestTrainee();
        storageMap.put("trainee:1", trainee);

        // when
        storage.remove("trainee:1");

        // then
        assertThat(storageMap).doesNotContainKey("trainee:1");
        assertThat(storageMap).doesNotContainValue(trainee);
    }

    @Test
    @DisplayName("Test of the method clear - should clear storage map")
    public void testClear() {
        // given
        var storageMap = storage.getStorage();
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        trainee2.setId(2L);
        storageMap.put("trainee:1", trainee1);
        storageMap.put("trainee:2", trainee2);

        // when
        storage.clear();

        // then
        assertThat(storageMap).isEmpty();
    }

    @Test
    @DisplayName("Test of the method size - should return size of storage map")
    public void testSizeOfEmptyStorage() {
        // when
        var actualResult = storage.size();

        // then
        assertThat(actualResult).isZero();
    }

    @Test
    @DisplayName("Test of the method size - should return size of non-empty storage map")
    public void testSizeOfNonEmptyStorage() {
        // given
        var storageMap = storage.getStorage();
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        storageMap.put("trainee:1", trainee1);
        storageMap.put("trainee:2", trainee2);

        // when
        var actualResult = storage.size();

        // then
        assertThat(actualResult).isEqualTo(2);
    }

    private Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");

        return trainee;
    }

}