package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.dto.TokensResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface TokenResponseDtoMapper {
    @Mapping(source = "accessToken", target = "accessToken")
    @Mapping(source = "refreshToken", target = "refreshToken")
    TokensResponseDto toDto(Map<String, String> tokens);
}
