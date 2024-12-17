package org.springframework.samples.petclinic.care;


import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.pet.Visit;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
//test 2
public class CareProvision extends BaseEntity {

    @Pattern(regexp = "^Care rated with [0-9] stars.*$")
    String userRating;

    @ManyToOne
    Visit visit;

    @ManyToOne
    @NotNull
    Care care;
}
