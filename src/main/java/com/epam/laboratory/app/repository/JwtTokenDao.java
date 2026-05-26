package com.epam.laboratory.app.repository;

import com.epam.laboratory.app.aspect.annotation.Logging;
import com.epam.laboratory.app.security.JwtToken;
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
public interface JwtTokenDao extends JpaRepository<JwtToken, UUID> {
     String FIND_BY_LAST_NOT_REVOKED_BY_USERNAME_QUERY = "SELECT t FROM JwtToken t WHERE t.payload.sub = :username AND t.payload.jtt = :type AND t.revoked = false ORDER BY t.id DESC";
     String REVOKE_QUERY = "UPDATE JwtToken t SET t.revoked = true WHERE t.id = :id";
     String IS_REVOKED_BY_ID_QUERY = "SELECT CASE WHEN (COUNT(t) > 0) THEN true ELSE false END FROM JwtToken t WHERE t.id = :id AND t.revoked = true";

     @Logging(Level.INFO)
     @Transactional(readOnly = true)
     @Query(FIND_BY_LAST_NOT_REVOKED_BY_USERNAME_QUERY)
     Optional<JwtToken> findLastNotRevokedByUsernameAndType(@Param("username") String username,
                                                            @Param("type") JwtToken.JwtTokenType type);

     @Logging(Level.INFO)
     @Query(REVOKE_QUERY)
     @Modifying
     void revoke(@Param("id") UUID tokenId);

     @Logging(Level.INFO)
     @Transactional(readOnly = true)
     @Query(IS_REVOKED_BY_ID_QUERY)
     boolean isRevokedById(@Param("id") UUID tokenId);

}
