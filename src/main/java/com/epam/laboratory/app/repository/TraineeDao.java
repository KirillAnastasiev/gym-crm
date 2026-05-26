package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Trainee;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.event.Level;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface TraineeDao extends UserDao<Trainee>, JpaRepository<Trainee, Long>, JpaSpecificationExecutor<Trainee> {
    String UPDATE_BY_USERNAME_QUERY = "UPDATE Trainee t SET t.firstName = :firstName, t.lastName = :lastName, t.dateOfBirth = :dateOfBirth, t.address = :address, t.active = :isActive WHERE t.username = :username";
    String CHANGE_STATUS_BY_USERNAME_QUERY = "UPDATE Trainee t SET t.active = :isActive WHERE t.username = :username";

    @Logging(Level.INFO)
    default Trainee updateByUsername(String username, Trainee trainee) {
        doUpdateByUsername(username, trainee.getFirstName(), trainee.getLastName(), trainee.getDateOfBirth(), trainee.getAddress(), trainee.getActive());
        return findByUsername(username).get();
    }

    @Logging(Level.INFO)
    @Query(UPDATE_BY_USERNAME_QUERY)
    @Modifying
    void doUpdateByUsername(@Param("username") String username,
                            @Param("firstName") String firstName,
                            @Param("lastName") String lastName,
                            @Param("dateOfBirth") LocalDate dateOfBirth,
                            @Param("address") String address,
                            @Param("isActive") boolean isActive);

    @Logging(Level.INFO)
    @Query(CHANGE_STATUS_BY_USERNAME_QUERY)
    @Modifying
    void changeStatusByUsername(@Param("username") String username,
                                @Param("isActive") boolean isActive);

}
