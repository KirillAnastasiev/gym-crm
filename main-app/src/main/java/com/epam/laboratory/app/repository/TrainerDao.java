package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.Trainer;
import com.epam.laboratory.app.domain.TrainingType;
import org.slf4j.event.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainerDao extends UserDao<Trainer>, JpaRepository<Trainer, Long>, JpaSpecificationExecutor<Trainer>{
    String UPDATE_BY_USERNAME_QUERY = "UPDATE Trainer t SET t.firstName = :firstName, t.lastName = :lastName, t.specialization = :specialization, t.active = :isActive WHERE t.username = :username";
    String CHANGE_STATUS_BY_USERNAME_QUERY = "UPDATE Trainer t SET t.active = :isActive WHERE t.username = :username";

    @Logging(Level.INFO)
    default Trainer updateByUsername(String username, Trainer trainer) {
        doUpdateByUsername(username, trainer.getFirstName(), trainer.getLastName(), trainer.getSpecialization(), trainer.getActive());
        return findByUsername(username).get();
    }

    @Logging(Level.INFO)
    @Query(UPDATE_BY_USERNAME_QUERY)
    @Modifying
    void doUpdateByUsername(@Param("username") String username,
                            @Param("firstName") String firstName,
                            @Param("lastName") String lastName,
                            @Param("specialization") TrainingType specialization,
                            @Param("isActive") Boolean isActive);

    @Logging(Level.INFO)
    @Query(CHANGE_STATUS_BY_USERNAME_QUERY)
    @Modifying
    void changeStatusByUsername(@Param("username") String username,
                                @Param("isActive") boolean isActive);

}
