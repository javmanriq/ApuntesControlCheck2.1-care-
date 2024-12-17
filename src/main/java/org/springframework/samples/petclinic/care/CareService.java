package org.springframework.samples.petclinic.care;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CareService {    

    @Autowired
    CareProvisionRepository cpr;
    public List<Care> getAllCares(){
        return cpr.findAllCares();
    }
    
    // Test 7
    @Transactional(readOnly = true)
    public List<Care> getAllCompatibleCares(PetType petTypeName, Care additionalCareName){
        return cpr.findCompatibleCares(petTypeName, additionalCareName);
    }
    
    @Transactional(readOnly = true)
    public Care getCare(String careName) {
        return cpr.findCareByName(careName);
    }
    
    //test 8
    @Transactional(rollbackFor = {NonCompatibleCaresException.class, UnfeasibleCareException.class})
    public CareProvision save(CareProvision p) throws NonCompatibleCaresException, UnfeasibleCareException {
        List<CareProvision> careProvidedDuringVisit = getCaresProvidedInVisitById(p.getVisit().getId());
        Care care = p.getCare();
        if (careProvidedDuringVisit.stream().anyMatch(cp -> cp.getCare().getIncompatibleCares().contains(care)))
            throw new NonCompatibleCaresException();
        if (!care.getCompatiblePetTypes().contains(p.getVisit().getPet().getType()))
            throw new UnfeasibleCareException();
        return cpr.save(p);
    }

    // Test 4
    public List<CareProvision> getAllCaresProvided(){
        return cpr.findAll();
    }

    //test 8
    @Transactional(readOnly = true)
    public List<CareProvision> getCaresProvidedInVisitById(Integer visitId){
        return cpr.findCaresProvidedByVisitId(visitId);

    }

    //test 9
    public Page<CareProvision> getPaginatedCareProvisions(Pageable pageable){
        return cpr.findAll(pageable);
    }

}
