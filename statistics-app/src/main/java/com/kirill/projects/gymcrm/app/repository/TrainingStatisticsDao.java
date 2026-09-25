package com.kirill.projects.gymcrm.app.repository;

import com.kirill.projects.gymcrm.app.domain.TrainingStatistics;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface TrainingStatisticsDao extends MongoRepository<TrainingStatistics, String> {
    @Query("{ '_id': ?0 }")
    void updateById(String id, TrainingStatistics trainingStatistics);

    Optional<TrainingStatistics> findByTrainerUsername(String trainerUsername);

    @Aggregation(pipeline = {
            "{ $match: { 'trainer_username': ?0 } }",
            "{ $unwind: '$years_list' }",
            "{ $match: { 'years_list.year': { $gte: ?1, $lte: ?3 } } }",
            "{ $unwind: '$years_list.months_list' }",
            "{ $match: { $or: [ " +
                    "{ $and: [ { 'years_list.year': ?1 }, { 'years_list.months_list.month': { $gte: ?2 } } ] }, " +
                    "{ $and: [ { 'years_list.year': { $gt: ?1, $lt: ?3 } } ] }, " +
                    "{ $and: [ { 'years_list.year': ?3 }, { 'years_list.months_list.month': { $lte: ?4 } } ] } " +
                    "] } }",
            "{ $group: { " +
                    "_id: '$_id', " +
                    "trainer_username: { $first: '$trainer_username' }, " +
                    "trainer_firstname: { $first: '$trainer_firstname' }, " +
                    "trainer_lastname: { $first: '$trainer_lastname' }, " +
                    "trainer_status: { $first: '$trainer_status' }, " +
                    "years_list: { $push: '$years_list' } " +
                    "} }"
    })
    Optional<TrainingStatistics> findByTrainerUsernameBetweenDates(String trainerUsername,
                                                                   int yearFrom,
                                                                   int monthFrom,
                                                                   int yearTo,
                                                                   int monthTo);
}
