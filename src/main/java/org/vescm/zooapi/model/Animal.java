package org.vescm.zooapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vescm.zooapi.enums.Kingdom;
import org.vescm.zooapi.enums.Phylum;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String specie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kingdom kingdom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Phylum phylum;

    @Column(nullable = false)
    private int quantity;
}
