package org.vescm.zooapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.vescm.zooapi.constants.AnimalConstants;
import org.vescm.zooapi.enums.Kingdom;
import org.vescm.zooapi.enums.Phylum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDto {
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    private String specie;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Kingdom kingdom;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Phylum phylum;

    @NotNull
    @Min(1)
    @Max(10)
    private int quantity;
}
