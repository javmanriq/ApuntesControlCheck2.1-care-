package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.samples.petclinic.care.CareProvision;
import org.springframework.samples.petclinic.care.CareProvisionRepository;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetRepository;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.pet.Visit;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class Test2 {

    @Autowired(required = false)
    CareProvisionRepository cr;
    @Autowired(required = false)
    EntityManager em;
    @Autowired
    PetRepository pr;
    

    @Test
    public void test2(){
        entityAndReporitoryExist();
        repositoryContainsMethod();
        testConstraints();
        testAnnotations();
    }

    
    public void entityAndReporitoryExist(){
        Entity entityAnnotation=CareProvision.class.getAnnotation(Entity.class);
        if(entityAnnotation==null)
            fail("The class CareProvision is not annotated as an entity");
        if(cr==null)
            fail("The CareProvision repository was not injected. Have you made it extend CrudRepository?");
    }        

    
    public void repositoryContainsMethod() {
        try {
            Method findAllCares = CareProvisionRepository.class.getDeclaredMethod("findAllCares");
            if(cr!=null){
                List<Care> pts= (List<Care>) findAllCares.invoke(cr);
                assertNotNull(pts,"We can not query all the cares through the repository");
            }else
                fail("The repository was not injected into the tests, its autowired value was null");
        } catch(NoSuchMethodException e) {
            fail("There is no method findAllCares in CareProvisionRepository", e);
        } catch (IllegalAccessException e) {
            fail("There is no public method findAllCares in CareProvisionRepository", e);
        } catch (IllegalArgumentException e) {
            fail("There is no method findAllCares in CareProvisionRepository", e);
        } catch (InvocationTargetException e) {
            fail("There is no method findAllCares in CareProvisionRepository", e);
        }
    }


     void testConstraints(){
        Visit visit=pr.findVisitById(1);

        PetType pt = pr.findPetTypes().get(0);
        HashSet<PetType> petTypes=new HashSet<>();
        petTypes.add(pt);
        Care care=new Care();
        care.setCompatiblePetTypes(petTypes);
        care.setName("Happy Puppy");        
        care.setCareDuration(10);        

        CareProvision f=new CareProvision();        
        f.setVisit(visit);
        f.setUserRating("Hello!");;
        f.setCare(care);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        assertFalse(validator.validate(f).isEmpty(),"The rating 'Hello!' should not be valid!");
        f.setUserRating("Care rated with B stars, I am very happy with the service");
        assertFalse(validator.validate(f).isEmpty(),"The careprovision provided should be valid");
        f.setUserRating("Care rated with 8 stars, I am very happy with the service");
        assertTrue(validator.validate(f).isEmpty(),"The careprovision provided should be valid");
        f.setVisit(null);
        assertTrue(validator.validate(f).isEmpty(),"CareProvisions without a Visit are valid");
        f.setCare(null);
        assertFalse(validator.validate(f).isEmpty(),"CareProvisions without a Care are not valid");
        
    }

    void testAnnotations(){
        try{
            Field visit=CareProvision.class.getDeclaredField("visit");
            visit.setAccessible(true);
            ManyToOne annotationManytoOne=visit.getAnnotation(ManyToOne.class);
            assertNotNull(annotationManytoOne,"The visit property is not properly annotated");
            Field care=CareProvision.class.getDeclaredField("care");
            ManyToOne annotationManytoOne2=visit.getAnnotation(ManyToOne.class);
            assertNotNull(annotationManytoOne2,"The care property is not properly annotated");
        }catch(NoSuchFieldException ex){
            fail("The CareProvision class should have a field that is not present: "+ex.getMessage());
        }
    }
}
