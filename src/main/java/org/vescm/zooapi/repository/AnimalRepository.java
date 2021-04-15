package org.vescm.zooapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vescm.zooapi.model.Animal;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findBySpecie(String specie);
}
