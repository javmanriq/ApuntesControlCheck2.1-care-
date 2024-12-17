package org.springframework.samples.petclinic.care;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//test 2
@Repository
public interface CareProvisionRepository extends CrudRepository<CareProvision, Integer> {
    List<CareProvision> findAll();
    Optional<CareProvision> findById(int id);
    CareProvision save(CareProvision p);

    @Query("SELECT cp.care FROM CareProvision cp")
    List<Care> findAllCares();

    @Query("SELECT c FROM Care c WHERE c.name = ?1")
    Care findCareByName(String careName);

    //Test 4
    @Query("SELECT c FROM Care c WHERE :petType MEMBER OF c.compatiblePetTypes AND :additionalCare NOT MEMBER OF c.incompatibleCares")
    List<Care> findCompatibleCares(PetType petType, Care additionalCare);

    //test 8
    @Query("SELECT c FROM CareProvision c WHERE c.visit.id = :visitId")
    List<CareProvision> findCaresProvidedByVisitId(Integer visitId);

    //test 9
    Page<CareProvision> findAll(Pageable pageable);

}
