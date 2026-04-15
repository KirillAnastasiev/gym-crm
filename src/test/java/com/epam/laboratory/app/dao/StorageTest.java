package com.epam.laboratory.app.dao;

import com.epam.laboratory.app.domain.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class StorageTest {
    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
    }

    @Test
    @DisplayName("Test of the method put - should put entity to storage map")
    void testPut() {
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
    void testGet_positive() {
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
    void testGet_negative() {
        // when
        var actualResult = storage.get("trainee:1");

        // then
        assertThat(actualResult).isNull();
    }

    @Test
    @DisplayName("Test of the method remove - should remove entity from storage map by key")
    void testRemove() {
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
    void testClear() {
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
    void testSizeOfEmptyStorage() {
        // when
        var actualResult = storage.size();

        // then
        assertThat(actualResult).isZero();
    }

    @Test
    @DisplayName("Test of the method size - should return size of non-empty storage map")
    void testSizeOfNonEmptyStorage() {
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

    @Test
    @DisplayName("Test of the method keySet - should return empty set if storage map is empty")
    void testKeySet_emptyStorage() {
        // when
        var actualResult = storage.keySet();

        // then
        assertThat(actualResult).isInstanceOf(Set.class);
        assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("Test of the method keySet - should return set of keys from storage map")
    void testKeySet_nonEmptyStorage() {
        // given
        var storageMap = storage.getStorage();
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        storageMap.put("trainee:1", trainee1);
        storageMap.put("trainee:2", trainee2);

        // when
        var actualResult = storage.keySet();

        // then
        assertThat(actualResult).isInstanceOf(Set.class);
        assertThat(actualResult).containsExactlyInAnyOrder("trainee:1", "trainee:2");
    }

    @Test
    @DisplayName("Test of the method values - should return empty collection if storage map is empty")
    void testValues_emptyStorage() {
        // when
        var actualResult = storage.values();

        // then
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).isEmpty();
    }

    @Test
    @DisplayName("Test of the method values - should return collection of values from storage map")
    void testValues_nonEmptyStorage() {
        // given
        var storageMap = storage.getStorage();
        var trainee1 = createTestTrainee();
        var trainee2 = createTestTrainee();
        trainee1.setId(1L);
        trainee2.setId(2L);
        storageMap.put("trainee:1", trainee1);
        storageMap.put("trainee:2", trainee2);

        // when
        var actualResult = storage.values();

        // then
        assertThat(actualResult).isInstanceOf(Collection.class);
        assertThat(actualResult).contains(trainee1, trainee2);
    }

    private Trainee createTestTrainee() {
        var trainee = new Trainee();
        trainee.setFirstName("FirstName");
        trainee.setLastName("LastName");
        trainee.setUsername("FirstName.LastName");

        return trainee;
    }

}