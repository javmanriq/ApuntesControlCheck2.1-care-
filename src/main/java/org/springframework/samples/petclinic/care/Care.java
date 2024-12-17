package org.springframework.samples.petclinic.care;

import java.util.Set;

import lombok.AccessLevel;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.pet.PetType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@Entity
//test 1
public class Care extends BaseEntity {
    @NotEmpty
    @Column(name = "name", unique = true)
    @Size(min = 3, max = 40)
    String name;

    @Column(name = "care_duration")
    @Min(1)
    @Max(120)
    int careDuration;

    @ManyToMany(cascade = CascadeType.ALL)
    @NotEmpty
    Set<PetType> compatiblePetTypes;

    //test 6
    @ManyToMany(cascade = CascadeType.ALL)
    Set<Care> incompatibleCares;

}
