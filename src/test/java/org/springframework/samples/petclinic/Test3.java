package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.care.CareProvision;
import org.springframework.samples.petclinic.care.CareProvisionRepository;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.stereotype.Service;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class Test3 {
    @Autowired
    CareProvisionRepository fr;
    
    @Test
    public void test3(){
        testInitialCareProvision();
        testInitialCares();
    }
    
    public void testInitialCareProvision(){
        List<CareProvision> careProvisions=fr.findAll();
        assertTrue(careProvisions.size()==2, "Exactly two care provisions should be present in the DB");

        Optional<CareProvision> p1=fr.findById(1);
        assertTrue(p1.isPresent(),"There should exist a care provision with id:1");
        assertEquals(p1.get().getVisit().getId(),1, "The care with id 1 should be associate to the visit with id 1");
        assertEquals("Care rated with 8 stars",p1.get().getUserRating(), "The rating of the care provision with id:1 should be 'Care rated with 8 stars'");
        assertEquals(p1.get().getCare().getName(),"Hair cut","The name of the care of the care provision with id:1 should be 'Hair cut'");

        Optional<CareProvision> p2=fr.findById(2);
        assertTrue(p2.isPresent(),"There should exist a care provision with id:2");
        assertEquals(2,p2.get().getVisit().getId(),"The care with id:2 should be associated to the visit with id 2");
        assertEquals("Care rated with 9 stars, I am quite happy!",p2.get().getUserRating(), "The rating of the care provision with id:2 should be 'Care rated with 9 stars, I am quite happy!'");
        assertEquals(p2.get().getCare().getName(),"Exotic shampoo cleaning","The name of the pet of the feeding with id:2 should be 'Exotic shampoo cleaning'");

    }

    public void testInitialCares()
    {
        List<Care> cares = new ArrayList<Care>();
        try {
            Method findAllCares = CareProvisionRepository.class.getDeclaredMethod("findAllCares");
            if(fr!=null){
                cares = (List<Care>) findAllCares.invoke(fr);
            }else
                fail("The repository was not injected into the tests, its autowired value was null");
        } catch(NoSuchMethodException e) {
            fail("There is no method findAllCares in CareProvisionRepository", e);
        } catch (IllegalAccessException e) {
            fail("There is no public method findAllCares in CareProvisionRepository", e);
        } catch (IllegalArgumentException e) {
            fail("There is no method findAllCares() in CareProvisionRepository", e);
        } catch (InvocationTargetException e) {
            fail("There is no method findAllCares() in CareProvisionRepository", e);
        }

        assertTrue(cares.size()==2,"Exactly two feeding types should be present in the DB");
        
        for(Care v:cares) {
            if(v.getName().equals("Hair cut")){
                assertEquals(v.getName(),"Hair cut","The name of the care with id:1 should be 'Hair cut'");
                assertEquals(v.getCareDuration(),30,"The duration of the care type with id:1 is not correct");
                assertTrue(containPetTypeNamed(v.getCompatiblePetTypes(),"dog"),"The care with id 1 is not associate to the pet type named 'dog'");
                assertTrue(containPetTypeNamed(v.getCompatiblePetTypes(),"hamster"),"The care with id 1 is not associate to the pet type named 'hamster'");

            }else if(v.getName().equals("Exotic shampoo cleaning")){
                assertEquals(v.getName(),"Exotic shampoo cleaning","The name of the care type with id:2 should be 'Chemical flea removal'");
                assertEquals(v.getCareDuration(),15,"The duration of the care with id:2 is not correct");
                assertTrue(containPetTypeNamed(v.getCompatiblePetTypes(),"hamster"),"The care with id 2 is not associate to the pet type named 'hamster'");
                assertTrue(containPetTypeNamed(v.getCompatiblePetTypes(),"cat"),"The care with id 2 is not associate to the pet type named 'cat'");
            }else {
                fail("The name of the care is note correct");
            }
        }
        
    }

    private Boolean containPetTypeNamed(Set<PetType> compatiblePetTypes, String string) {
        Boolean result=false;
        for(PetType pt:compatiblePetTypes)
            if(pt.getName().equals(string))
                result=true;
        return result;
    }
}