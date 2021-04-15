package org.vescm.zooapi.builder;

import lombok.Builder;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.enums.Kingdom;
import org.vescm.zooapi.enums.Phylum;

@Builder
public class AnimalDtoBuilder {
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String specie = "Dromedary";


    @Builder.Default
    private Kingdom kingdom = Kingdom.ANIMAL;


    @Builder.Default
    private Phylum phylum = Phylum.CHORDATA;


    @Builder.Default
    private int quantity = 7;

    public AnimalDto toAnimalDto() {
        return new AnimalDto(id,
                specie,
                kingdom,
                phylum,
                quantity);
    }
}
