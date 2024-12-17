package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.care.CareService;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class Test7 {
    @Autowired
    CareService cs;        
    @Autowired
    PetService ps;
    @Autowired EntityManager em;
    Care c;

   

    @Test
    public void test7(){
        // We  create an additional care for testing purposes         
        String petTypeName="dog";
        PetType pt=ps.getPetTypeByName(petTypeName);
        c=new Care();
        c.setName("Massage");
        c.setCareDuration(15);
        Set<PetType> pts=new HashSet<PetType>();
        pts.add(pt);
        c.setCompatiblePetTypes(pts);   
        em.persist(c);
        
        validateFindCaresByPetTypeAndAdditionalTreatment();
        validateFindCaresByPetType();        
        validateFindCaresByPetTypeWithoutAvailableCares();
    }


    public void validateFindCaresByPetTypeAndAdditionalTreatment(){
        String petTypeName="hamster";
        PetType pt=ps.getPetTypeByName(petTypeName);
        Care additionalCare=cs.getCare("Exotic shampoo cleaning");
        List<Care> cares=cs.getAllCompatibleCares(pt,additionalCare);
        assertNotNull(cares, "getAllCompatibleCares by petTypeName is returning null");
        assertFalse(cares.isEmpty(), "The set of compabile cares for petType 'hamster' with  additional care 'Exotic shampoo cleaning' should not be empty");
        assertEquals(cares.size(),1, "The set of compabile cares for petType 'hamster' with care 'Exotic shampoo cleaning' should have a single element");
        assertEquals(additionalCare,cares.get(0),"The single compatible care is 'Exotic shampoo cleaning' itself");
    }

    public void validateFindCaresByPetType(){
        String petTypeName="dog";
        PetType pt=ps.getPetTypeByName(petTypeName);        
        List<Care> cares=cs.getAllCompatibleCares(pt,c);
        assertNotNull(cares, "getAllCompatibleCares by petType is returning null");
        assertFalse(cares.isEmpty(), "The set of compabile cares for petType 'dog' with  additional care 'Massage' is empty, but it should have 2 elements");
        assertEquals(cares.size(),2, "The set of compabile cares for petType 'dog' with  additional care 'Massage' is empty, but it should have 2 elements");
        Care otherCare=cs.getCare("Hair cut");
        assertTrue(cares.contains(otherCare),"'HairCut' should be compatible with additional care 'Massage'");
        assertTrue(cares.contains(c),"'Massage' should be compatible with additional care 'Massage'");
    }    
    
    public void validateFindCaresByPetTypeWithoutAvailableCares(){
        PetType pt=ps.getPetTypeByName("snake"); 
        List<Care> cares=cs.getAllCompatibleCares(pt,c);

        assertNotNull(cares, "getAllCompatibleCares by petTypeName is returning null");
        assertTrue(cares.isEmpty(), "The set of compabile cares for petType 'snake' should be empty");
    }

}
