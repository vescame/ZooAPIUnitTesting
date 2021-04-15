package org.vescm.zooapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.model.Animal;

import java.util.List;

@Mapper
public interface AnimalMapper {
    AnimalMapper INSTANCE = Mappers.getMapper(AnimalMapper.class);
    Animal toModel(AnimalDto animalDto);
    AnimalDto toDto(Animal animal);
    List<AnimalDto> toListDto(List<Animal> animals);
    List<Animal> toModel(List<AnimalDto> animals);
}