package org.vescm.zooapi.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vescm.zooapi.constants.AnimalConstants;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.exception.AnimalAlreadyExistsException;
import org.vescm.zooapi.exception.AnimalLimitExceededException;
import org.vescm.zooapi.exception.AnimalNumberAlreadyZeroException;
import org.vescm.zooapi.mapper.AnimalMapper;
import org.vescm.zooapi.model.Animal;
import org.vescm.zooapi.exception.AnimalNotFoundException;
import org.vescm.zooapi.repository.AnimalRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final AnimalMapper animalMapper = AnimalMapper.INSTANCE;

    public Animal animalExistsById(Long id) throws AnimalNotFoundException{
        Optional<Animal> animal = this.animalRepository.findById(id);
        if (animal.isEmpty()) {
            throw new AnimalNotFoundException(" with id: " + id);
        }
        return animal.get();
    }

    public AnimalDto getById(Long id) throws AnimalNotFoundException {
        return animalMapper.toDto(animalExistsById(id));
    }

    private boolean canCreateAnimal(String specie) {
        return animalRepository.findBySpecie(specie).isEmpty();
    }

    public AnimalDto createAnimal(AnimalDto animalDto) throws AnimalAlreadyExistsException {
        if (canCreateAnimal(animalDto.getSpecie())) {
            return animalMapper.toDto(animalRepository.save(animalMapper.toModel(animalDto)));

        }
        throw new AnimalAlreadyExistsException(animalDto.getSpecie());
    }

    private Animal animalExistsBySpecie(String specie) throws AnimalNotFoundException {
        Optional<Animal> animal = this.animalRepository.findBySpecie(specie);
        if (animal.isEmpty()) {
            throw new AnimalNotFoundException(" of specie: " + specie);
        }
        return animal.get();
    }

    public AnimalDto getBySpecie(String specie) throws AnimalNotFoundException {
        return animalMapper.toDto(animalExistsBySpecie(specie));
    }

    public void deleteById(Long id) throws AnimalNotFoundException {
        Animal animal = animalExistsById(id);
        animalRepository.deleteById(id);
    }

    public List<AnimalDto> listAll() {
        return animalMapper.toListDto(animalRepository.findAll());
    }

    public AnimalDto increaseAnimalNumber(Long id, int quantity)
            throws AnimalNotFoundException, AnimalLimitExceededException {
        Animal animal = animalExistsById(id);
        int postIncrementQuantity = quantity + animal.getQuantity();
        if (postIncrementQuantity > AnimalConstants.MAX) {
            throw new AnimalLimitExceededException(animal.getSpecie());
        }
        animal.setQuantity(postIncrementQuantity);
        return animalMapper.toDto(animalRepository.save(animal));
    }

    public AnimalDto decreaseAnimalNumber(Long id, int quantity)
            throws AnimalNotFoundException, AnimalNumberAlreadyZeroException {
        Animal animal = animalExistsById(id);
        int postDecrementQuantity = animal.getQuantity() - quantity;
        if (postDecrementQuantity < AnimalConstants.MIN) {
            throw new AnimalNumberAlreadyZeroException();
        }
        animal.setQuantity(postDecrementQuantity);
        return animalMapper.toDto(animalRepository.save(animal));
    }
}
