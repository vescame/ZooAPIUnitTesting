package org.vescm.zooapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vescm.zooapi.builder.AnimalDtoBuilder;
import org.vescm.zooapi.constants.AnimalConstants;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.exception.AnimalAlreadyExistsException;
import org.vescm.zooapi.exception.AnimalLimitExceededException;
import org.vescm.zooapi.exception.AnimalNumberAlreadyZeroException;
import org.vescm.zooapi.exception.AnimalNotFoundException;
import org.vescm.zooapi.mapper.AnimalMapper;
import org.vescm.zooapi.model.Animal;
import org.vescm.zooapi.repository.AnimalRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTests {
    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private AnimalRepository animalRepository;

    private AnimalMapper animalMapper = AnimalMapper.INSTANCE;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void whenAnimalInformedThenItShouldBeCreated() throws AnimalAlreadyExistsException {
        // given
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedSavedAnimal = animalMapper.toModel(expectedAnimalDto);

        // when
        when(animalRepository.findBySpecie(expectedAnimalDto.getSpecie())).thenReturn(empty());
        when(animalRepository.save(expectedSavedAnimal)).thenReturn(expectedSavedAnimal);

        //then
        AnimalDto createdAnimalDto = animalService.createAnimal(expectedAnimalDto);

        assertThat(createdAnimalDto.getId(), is(equalTo(expectedAnimalDto.getId())));
        assertThat(createdAnimalDto.getSpecie(), is(equalTo(expectedAnimalDto.getSpecie())));
        assertThat(createdAnimalDto.getQuantity(), is(equalTo(expectedAnimalDto.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredAnimalInformedThenAnExceptionShouldBeThrown() {
        // given
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal duplicatedAnimal = animalMapper.toModel(expectedAnimalDto);

        // when
        when(animalRepository.findBySpecie(expectedAnimalDto.getSpecie())).thenReturn(Optional.of(duplicatedAnimal));

        // then
        assertThrows(AnimalAlreadyExistsException.class, () -> animalService.createAnimal(expectedAnimalDto));
    }

    @Test
    void whenValidAnimalNameIsGivenThenReturnAAnimal() throws AnimalNotFoundException {
        // given
        AnimalDto expectedFoundAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedFoundAnimal = animalMapper.toModel(expectedFoundAnimalDto);

        // when
        when(animalRepository.findBySpecie(expectedFoundAnimal.getSpecie())).thenReturn(Optional.of(expectedFoundAnimal));

        // then
        AnimalDto foundAnimalDto = animalService.getBySpecie(expectedFoundAnimalDto.getSpecie());

        assertThat(foundAnimalDto, is(equalTo(expectedFoundAnimalDto)));
    }

    @Test
    void whenNotRegisteredAnimalNameIsGivenThenThrowAnException() {
        // given
        AnimalDto expectedFoundAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        // when
        when(animalRepository.findBySpecie(expectedFoundAnimalDto.getSpecie())).thenReturn(empty());

        // then
        assertThrows(AnimalNotFoundException.class, () -> animalService.getBySpecie(expectedFoundAnimalDto.getSpecie()));
    }

    @Test
    void whenListAnimalIsCalledThenReturnAListOfAnimals() {
        // given
        AnimalDto expectedFoundAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedFoundAnimal = animalMapper.toModel(expectedFoundAnimalDto);

        //when
        when(animalRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundAnimal));

        //then
        List<AnimalDto> foundListAnimalsDTO = animalService.listAll();

        assertThat(foundListAnimalsDTO, is(not(empty())));
        assertThat(foundListAnimalsDTO.get(0), is(equalTo(expectedFoundAnimalDto)));
    }

    @Test
    void whenListAnimalIsCalledThenReturnAnEmptyListOfAnimals() {
        //when
        when(animalRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<AnimalDto> foundListAnimalsDTO = animalService.listAll();

        assertThat(foundListAnimalsDTO, is(Collections.EMPTY_LIST));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenAAnimalShouldBeDeleted() throws AnimalNotFoundException {
        // given
        AnimalDto expectedDeletedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedDeletedAnimal = animalMapper.toModel(expectedDeletedAnimalDto);

        // when
        when(animalRepository.findById(expectedDeletedAnimalDto.getId())).thenReturn(Optional.of(expectedDeletedAnimal));
        doNothing().when(animalRepository).deleteById(expectedDeletedAnimalDto.getId());

        // then
        animalService.deleteById(expectedDeletedAnimalDto.getId());

        verify(animalRepository, times(1)).findById(expectedDeletedAnimalDto.getId());
        verify(animalRepository, times(1)).deleteById(expectedDeletedAnimalDto.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementAnimalStock() throws AnimalNotFoundException, AnimalLimitExceededException {
        //given
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedAnimal = animalMapper.toModel(expectedAnimalDto);

        //when
        when(animalRepository.findById(expectedAnimalDto.getId())).thenReturn(Optional.of(expectedAnimal));
        when(animalRepository.save(expectedAnimal)).thenReturn(expectedAnimal);

        int quantityToIncrement = 2;
        int expectedQuantityAfterIncrement = expectedAnimalDto.getQuantity() + quantityToIncrement;

        // then
        AnimalDto incrementedAnimalDto = animalService.increaseAnimalNumber(expectedAnimalDto.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedAnimalDto.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(AnimalConstants.MAX));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedAnimal = animalMapper.toModel(expectedAnimalDto);

        when(animalRepository.findById(expectedAnimalDto.getId())).thenReturn(Optional.of(expectedAnimal));

        int quantityToIncrement = 80;
        assertThrows(AnimalLimitExceededException.class, () -> animalService.increaseAnimalNumber(expectedAnimalDto.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedAnimal = animalMapper.toModel(expectedAnimalDto);

        when(animalRepository.findById(expectedAnimalDto.getId())).thenReturn(Optional.of(expectedAnimal));

        int quantityToIncrement = 45;
        assertThrows(AnimalLimitExceededException.class, () -> animalService.increaseAnimalNumber(expectedAnimalDto.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(animalRepository.findById(INVALID_BEER_ID)).thenReturn(empty());

        assertThrows(AnimalNotFoundException.class, () -> animalService.increaseAnimalNumber(INVALID_BEER_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementAnimalStock() throws AnimalNotFoundException, AnimalNumberAlreadyZeroException {
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedAnimal = animalMapper.toModel(expectedAnimalDto);

        when(animalRepository.findById(expectedAnimalDto.getId())).thenReturn(Optional.of(expectedAnimal));
        when(animalRepository.save(expectedAnimal)).thenReturn(expectedAnimal);

        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedAnimalDto.getQuantity() - quantityToDecrement;
        AnimalDto incrementedAnimalDto = animalService.decreaseAnimalNumber(expectedAnimalDto.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedAnimalDto.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyAnimalStock() throws AnimalNotFoundException, AnimalNumberAlreadyZeroException {
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedAnimal = animalMapper.toModel(expectedAnimalDto);

        when(animalRepository.findById(expectedAnimalDto.getId())).thenReturn(Optional.of(expectedAnimal));
        when(animalRepository.save(expectedAnimal)).thenReturn(expectedAnimal);

        int quantityToDecrement = 7;
        int expectedQuantityAfterDecrement = expectedAnimalDto.getQuantity() - quantityToDecrement;
        AnimalDto incrementedAnimalDto = animalService.decreaseAnimalNumber(expectedAnimalDto.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(0));
        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedAnimalDto.getQuantity()));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        AnimalDto expectedAnimalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        Animal expectedAnimal = animalMapper.toModel(expectedAnimalDto);

        when(animalRepository.findById(expectedAnimalDto.getId())).thenReturn(Optional.of(expectedAnimal));

        int quantityToDecrement = 80;
        assertThrows(AnimalNumberAlreadyZeroException.class, () -> animalService.decreaseAnimalNumber(expectedAnimalDto.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(animalRepository.findById(INVALID_BEER_ID)).thenReturn(empty());

        assertThrows(AnimalNotFoundException.class, () -> animalService.decreaseAnimalNumber(INVALID_BEER_ID, quantityToDecrement));
    }
}
