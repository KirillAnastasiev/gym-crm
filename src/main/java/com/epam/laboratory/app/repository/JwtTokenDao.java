package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.domain.JwtTokenEntity;
import org.slf4j.event.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface JwtTokenDao extends JpaRepository<JwtTokenEntity, UUID> {
     String REVOKE_BY_USERNAME_QUERY = "UPDATE JwtTokenEntity t SET t.isRevoked = true WHERE t.username = :username";
     String IS_REVOKED_BY_ID_QUERY = "SELECT CASE WHEN (COUNT(t) > 0) THEN true ELSE false END FROM JwtTokenEntity t WHERE t.id = :id AND t.isRevoked = true";

     @Logging(Level.INFO)
     @Transactional(readOnly = true)
     Optional<JwtTokenEntity> findTopByUsernameAndTokenTypeAndIsRevokedFalseOrderByIdDesc(String username, String tokenType);

     @Logging(Level.INFO)
     @Query(REVOKE_BY_USERNAME_QUERY)
     @Modifying
     void revokeByUsername(@Param("username") String username);

     @Logging(Level.INFO)
     @Transactional(readOnly = true)
     @Query(IS_REVOKED_BY_ID_QUERY)
     boolean isRevokedById(@Param("id") UUID tokenId);

}
