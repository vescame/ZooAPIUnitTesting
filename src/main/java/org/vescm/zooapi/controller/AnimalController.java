package org.vescm.zooapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.dto.QuantityDto;
import org.vescm.zooapi.exception.AnimalAlreadyExistsException;
import org.vescm.zooapi.exception.AnimalLimitExceededException;
import org.vescm.zooapi.exception.AnimalNumberAlreadyZeroException;
import org.vescm.zooapi.exception.AnimalNotFoundException;
import org.vescm.zooapi.service.AnimalService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/animals")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AnimalController {
    private final AnimalService animalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalDto addAnimal(@RequestBody @Valid AnimalDto animalDto) throws AnimalAlreadyExistsException {
        return animalService.createAnimal(animalDto);
    }

    @GetMapping("/{name}")
    public AnimalDto findAnimal(@PathVariable String name) throws AnimalNotFoundException {
        return animalService.getBySpecie(name);
    }

    @GetMapping
    public List<AnimalDto> listAnimals() {
        return animalService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws AnimalNotFoundException {
        animalService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public AnimalDto increment(@PathVariable Long id, @RequestBody @Valid QuantityDto quantity)
            throws AnimalNotFoundException, AnimalLimitExceededException {
        return animalService.increaseAnimalNumber(id, quantity.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public AnimalDto decrement(@PathVariable Long id, @RequestBody @Valid QuantityDto quantityDTO)
            throws AnimalNotFoundException, AnimalNumberAlreadyZeroException {
        return animalService.decreaseAnimalNumber(id, quantityDTO.getQuantity());
    }
}
