package com.epam.laboratory.app.dto.mapper;

import com.epam.laboratory.app.domain.Training;
import com.epam.laboratory.app.dto.TrainingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TraineeWithoutTrainersMapper.class, TrainerWithoutTraineesMapper.class, TrainingTypeMapper.class})
public interface TrainingMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainee", target = "trainee")
    @Mapping(source = "trainer", target = "trainer")
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingType", target = "trainingType")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    TrainingDto toDto(Training training);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainee", target = "trainee")
    @Mapping(source = "trainer", target = "trainer")
    @Mapping(source = "trainingName", target = "trainingName")
    @Mapping(source = "trainingType", target = "trainingType")
    @Mapping(source = "trainingDate", target = "trainingDate")
    @Mapping(source = "trainingDuration", target = "trainingDuration")
    Training toEntity(TrainingDto trainingDto);
}
