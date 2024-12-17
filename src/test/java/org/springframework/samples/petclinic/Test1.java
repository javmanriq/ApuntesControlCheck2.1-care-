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
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.care.CareProvisionRepository;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.samples.petclinic.pet.PetRepository;
import org.springframework.samples.petclinic.pet.PetType;

@DataJpaTest
public class Test1 {
    
    @Autowired
    PetRepository pr;
    @Autowired(required = false)
    EntityManager em;
    
    @Test
    public void test1(){
        entityExists();
        testConstraints();
        testAnnotations();
    }

   
    public void entityExists() {
        Entity entityAnnotation=Care.class.getAnnotation(Entity.class);
        if(entityAnnotation==null)
            fail("The class Care is not annotated as an entity");    
    }  
    
    
    public void testConstraints(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        PetType pt = pr.findPetTypes().get(0);
        HashSet<PetType> petTypes=new HashSet<>();
        petTypes.add(pt);
        Care care=new Care();       
        care.setCompatiblePetTypes(petTypes);
        care.setName("ja");
        care.setCareDuration(10);
        care.getCompatiblePetTypes().add(pt);
        assertFalse(validator.validate(care).isEmpty(), "It should not allow names shorter than 5");

        care=new Care();       
        care.setCompatiblePetTypes(petTypes);
        care.setName("En un lugar de la mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidalgo de los de lanza en astillero, adarga antigua, rocín flaco y galgo corredor.");
        care.setCareDuration(10);
        care.getCompatiblePetTypes().add(pt);
        assertFalse(validator.validate(care).isEmpty(), "It should not allow names longer than 30");


        care=new Care();       
        care.setCompatiblePetTypes(petTypes);
        care.setCareDuration(10);
        care.getCompatiblePetTypes().add(pt);
        assertFalse(validator.validate(care).isEmpty(), "It should not allow empty names");

        care=new Care();
        care.setCompatiblePetTypes(petTypes);       
        care.setName("Jander Clander");
        care.setCareDuration(-10);
        care.getCompatiblePetTypes().add(pt);
        assertFalse(validator.validate(care).isEmpty(), "It should not allow negative durations");
        care.setCareDuration(180);
        assertFalse(validator.validate(care).isEmpty(), "It should not allow durations of more than 120 minutes");

        care=new Care();       
        care.setCompatiblePetTypes(new HashSet<>());
        care.setName("Jander Clander");        
        care.setCareDuration(10);
        assertFalse(validator.validate(care).isEmpty(), "It should not allow empty set of suitable pet types for a care");        
    }

    void testAnnotations(){
        try{
            Field petTypes=Care.class.getDeclaredField("compatiblePetTypes");
            petTypes.setAccessible(true);
            ManyToMany annotationManytoMany=petTypes.getAnnotation(ManyToMany.class);
            assertNotNull(annotationManytoMany,"The compatiblePetTypes property is not properly annotated");
            assertTrue(annotationManytoMany.cascade().length>0,"The cascade property is not properly configured");
            assertEquals(CascadeType.ALL,annotationManytoMany.cascade()[0],"The cascade property is not properly configured");            

            Field nameField=Care.class.getDeclaredField("name");
            nameField.setAccessible(true);
            Column columAnnotation=nameField.getAnnotation(Column.class);
            assertNotNull(annotationManytoMany,"The name property is not properly annotated");
            assertTrue(columAnnotation.unique(),"The name property is not annotated to be unique!");
        }catch(NoSuchFieldException ex){
            fail("The Care class should have a field that is not present: "+ex.getMessage());
        }
    }    

}
