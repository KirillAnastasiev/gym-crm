package com.epam.laboratory.app.security.mapper;

import com.epam.laboratory.app.domain.JwtTokenEntity;
import com.epam.laboratory.app.security.JwtToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JwtTokenMapper {
    @Mapping(source = "payload.jti", target = "id")
    @Mapping(source = "payload.jtt", target = "tokenType")
    @Mapping(source = "payload.exp", target = "expiryDate")
    @Mapping(source = "payload.sub", target = "username")
    @Mapping(source = "revoked", target = "isRevoked")
    JwtTokenEntity toEntity(JwtToken jwtToken);

    default String map(JwtToken.JwtTokenType type) {
        return type !=  null ? type.name() : null;
    }
}
