package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.care.CareProvision;
import org.springframework.samples.petclinic.care.CareProvisionRepository;
import org.springframework.samples.petclinic.care.CareService;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class Test6 {
    @Autowired
    CareProvisionRepository fr;

    @Test
    public void test6() {
        testAnnotations();
        testIncompatibleCaresLinked();
    }

    void testAnnotations(){
        try{
            Field incompatibleCares=Care.class.getDeclaredField("incompatibleCares");
            if(incompatibleCares==null)
                fail("The Care class should have an attribute called incompatibleCares that is not present");
            incompatibleCares.setAccessible(true);
            ManyToMany annotationManytoMany=incompatibleCares.getAnnotation(ManyToMany.class);
            assertNotNull(annotationManytoMany,"The incompatibleCares property is not properly annotated");
            assertEquals(CascadeType.ALL,annotationManytoMany.cascade()[0],"The cascade property is not properly configured");
        }catch(NoSuchFieldException ex){
            fail("The Care class should have an attribute called incompatibleCares that is not present: "+ex.getMessage());
        }
    }    

    void testIncompatibleCaresLinked() {
        Field incompatibleCares;
        Set<Care> ft;

        try {
            incompatibleCares = Care.class.getDeclaredField("incompatibleCares");
            if(incompatibleCares==null)
                fail("The Care class should have an attribute called incompatibleCares that is not present");
            incompatibleCares.setAccessible(true);
            List<Care> cares=findAllCares();
            assertFalse(cares.isEmpty(),"No cares found in the DB");
            Care c1=cares.get(0);
            assertTrue(c1!=null,"No Carewas found");
            ft =  (Set<Care>) incompatibleCares.get(c1);
            assertNotNull(ft,"The incompatible cares for the care with id:1 has not a feeding type associated");
            assertEquals(1,ft.size(),"The care with name "+c1.getName()+" has not incompatible cares.");
        } catch (NoSuchFieldException e) {
            fail("The Care class should have an attribute called incompatibleCares that is not present: "+e.getMessage());
        } catch (IllegalArgumentException e) {
            fail("The Care class should have an attribute called incompatibleCares that is not present: "+e.getMessage());
        } catch (IllegalAccessException e) {
            fail("The Care class should have an attribute called incompatibleCares that is not present: "+e.getMessage());
        }
    }

    private List<Care> findAllCares(){
        List<Care> pts=new ArrayList<Care>();
        try {
            Method findAllCares = CareProvisionRepository.class.getDeclaredMethod("findAllCares");
            if(fr!=null){
                pts= (List<Care>) findAllCares.invoke(fr);
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
        return pts;
    }


}
