package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.care.CareProvision;
import org.springframework.samples.petclinic.care.CareService;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.samples.petclinic.care.NonCompatibleCaresException;
import org.springframework.samples.petclinic.care.UnfeasibleCareException;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.pet.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class Test8 {

    @Autowired
    CareService cs;
    @Autowired
    PetService ps;
    @Autowired
    EntityManager em;

    @Transactional
    @Test
    public void test8(){
        testSaveCareProvisionSuccessfull();
        testSaveUnfeasibleCareProvision();
        testSaveIncompatibleCareProvision();
        testAnnotations();
    }

    private void testSaveIncompatibleCareProvision() {
       
        List<Visit> visits=ps.findVisitsByPetId(7);
        Visit visit=visits.get(1);

        PetType pt=visit.getPet().getType();
        Set<PetType> petTypes=new HashSet<PetType>();
        petTypes.add(pt);
        Care c1=new Care();
        c1.setName("AnExampleCare");
        c1.setCareDuration(15);
        c1.setCompatiblePetTypes(petTypes);
        em.persist(c1);
        
        Care c2=new Care();
        c2.setName("Another care");
        c2.setCareDuration(30);
        c2.setCompatiblePetTypes(petTypes);
        em.persist(c2);
        
        //Set<Care> incompatibleCaresC2=new HashSet<Care>();
        //incompatibleCaresC2.add(c1);
        addIncompatibleCare(c1,c2);
        addIncompatibleCare(c2,c1);
        //Set<Care> incompatibleCaresC1=new HashSet<Care>();
        //incompatibleCaresC1.add(c2);
        //c1.setIncompatibleCares(incompatibleCaresC1);
        em.persist(c1);
        em.persist(c2);

        
        CareProvision cp1=new CareProvision();
        cp1.setCare(c1);
        cp1.setUserRating("Care rated with 8 stars");
        cp1.setVisit(visit);

        try {
            cs.save(cp1);
        } catch (NonCompatibleCaresException e) {
            fail("this care should be compatible");
        } catch (UnfeasibleCareException e) {
            fail("this care should be feasible");
        }

        CareProvision cp2=new CareProvision();
        cp2.setCare(c2);
        cp2.setUserRating("Care rated with 9 stars");
        cp2.setVisit(visit);

        assertThrows(NonCompatibleCaresException.class, ()->cs.save(cp2));        

    }
    

    public void testSaveCareProvisionSuccessfull()
    {
        List<Visit> visits=ps.findVisitsByPetId(8);
        Visit visit=visits.get(0);

        PetType pt=visit.getPet().getType();
        Set<PetType> petTypes=new HashSet<PetType>();
        petTypes.add(pt);
        Care c1=new Care();
        c1.setName("AndThisIsACare");
        c1.setCareDuration(15);
        c1.setCompatiblePetTypes(petTypes);
        setIncompatibleCares(c1,new HashSet<Care>());
        Care c2=new Care();
        c2.setName("ThisIsAnotherCare");
        c2.setCareDuration(30);
        c2.setCompatiblePetTypes(petTypes);
        setIncompatibleCares(c2,new HashSet<Care>());
        
        Set<Care> incompatibleCaresC2=new HashSet<Care>();
        setIncompatibleCares(c2, incompatibleCaresC2);
        
        Set<Care> incompatibleCaresC1=new HashSet<Care>();
        setIncompatibleCares(c2, incompatibleCaresC1);
        em.persist(c1);
        em.persist(c2);

        
        CareProvision cp1=new CareProvision();
        cp1.setCare(c1);
        cp1.setUserRating("Care rated with 8 stars");
        cp1.setVisit(visit);

        try {
            cs.save(cp1);
        } catch (NonCompatibleCaresException e) {
            fail("this care should be compatible and you are throwing a NonCompabileCaresException");
        } catch (UnfeasibleCareException e) {
            fail("this care should be feasible and you are throwing an UnfeasibleCareException");
        }        

    }

    private void setIncompatibleCares(Care c, Set<Care> incompatibleCares) {
        try {
            Field incompatibleCaresAttribute = Care.class.getDeclaredField("incompatibleCares");
            incompatibleCaresAttribute.setAccessible(true);
            incompatibleCaresAttribute.set(c,incompatibleCares );
        } catch (NoSuchFieldException e) {
            fail("The Cares class should have an attribute called incompatibleCares that is not present: ", e);
        } catch (IllegalArgumentException e) {
            fail("The Cares class should have an attribute called incompatibleCares that is not present: ", e);
        } catch (IllegalAccessException e) {
            fail("The Cares class should have an attribute called incompatibleCares that is not present: ", e);
        }
    }

    private void addIncompatibleCare(Care c1, Care c2) {
        try {
            Field incompatibleCaresAttribute = Care.class.getDeclaredField("incompatibleCares");
            incompatibleCaresAttribute.setAccessible(true);
            Set<Care> cares=(Set<Care>) incompatibleCaresAttribute.get(c1);
            if(cares==null){
                cares=new HashSet<Care>();
                incompatibleCaresAttribute.set(c1,cares);
            }
            cares.add(c2);
        } catch (NoSuchFieldException e) {
            fail("The Cares class should have an attribute called incompatibleCares that is not present: ", e);
        } catch (IllegalArgumentException e) {
            fail("The Cares class should have an attribute called incompatibleCares that is not present: ", e);
        } catch (IllegalAccessException e) {
            fail("The Cares class should have an attribute called incompatibleCares that is not present: ", e);
        }
    }


    
    public void testSaveUnfeasibleCareProvision()   {
        List<Visit> visits=ps.findVisitsByPetId(8);
        Visit visit=visits.get(1);

        PetType pt=visit.getPet().getType();
        PetType differentPetType=null;
        List<PetType> petTypes=ps.findPetTypes();
        for(PetType candidate:petTypes)
            if(!candidate.equals(pt))
                differentPetType=candidate;

        Set<PetType> compatiblePetTypes=new HashSet<PetType>();
        compatiblePetTypes.add(differentPetType);
        Care c1=new Care();
        c1.setName("I think thi is the last one");
        c1.setCareDuration(10);
        c1.setCompatiblePetTypes(compatiblePetTypes);
        setIncompatibleCares(c1, new HashSet<Care>());
        em.persist(c1);
        CareProvision cp1=new CareProvision();
        cp1.setCare(c1);
        cp1.setUserRating("Care rated with 8 stars");
        cp1.setVisit(visit);


        assertThrows(UnfeasibleCareException.class,
            () -> cs.save(cp1));
    }

    public void testAnnotations() {
        Method save=null;
        try {
            save = CareService.class.getDeclaredMethod("save", CareProvision.class);
        } catch (NoSuchMethodException e) {
           fail("FeedingService does not have a save method");
        } catch (SecurityException e) {
            fail("save method is not accessible in FeedingService");
        }
        Transactional transactionalAnnotation=save.getAnnotation(Transactional.class);
        assertNotNull(transactionalAnnotation,"The method save is not annotated as transactional");
        List<Class<? extends Throwable>> exceptionsWithRollbackFor= Arrays.asList(transactionalAnnotation.rollbackFor());
        assertTrue(exceptionsWithRollbackFor.contains(NonCompatibleCaresException.class),"NonCompatibleCaresExceptions do not generate rollbacks in your save method!");
        assertTrue(exceptionsWithRollbackFor.contains(UnfeasibleCareException.class),"UnfeasibleCareExceptions do not generate rollbacks in your save method!");
    }    

}
