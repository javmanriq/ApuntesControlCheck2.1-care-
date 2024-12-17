package org.springframework.samples.petclinic.product;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.samples.petclinic.care.CareController;
import org.springframework.samples.petclinic.care.CareFormatter;
import org.springframework.samples.petclinic.care.CareProvision;
import org.springframework.samples.petclinic.care.CareService;
import org.springframework.samples.petclinic.care.ExceptionHandlingControllerAdvice;
import org.springframework.samples.petclinic.care.NonCompatibleCaresException;
import org.springframework.samples.petclinic.care.UnfeasibleCareException;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetFormatter;
import org.springframework.samples.petclinic.pet.PetService;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.pet.Visit;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CareController.class,
		includeFilters = {@ComponentScan.Filter(value = CareFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
            @ComponentScan.Filter(value = PetFormatter.class, type = FilterType.ASSIGNABLE_TYPE)},
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
		excludeAutoConfiguration= SecurityConfiguration.class)
public class Test10 {
    @MockBean
    CareService careService;
    @MockBean
	PetService petService;
    @Autowired
    private MockMvc mockMvc;

    Care care;
    Care care2;
    Visit v;

    private static final String SHELL_POLISHING = "Tortoise shell polishing";
    private static final String BRANQUIAL_CLEANING="Cleaning of the branquea of your fish";
    private static final String SACRIFICE = "Pity Sacrifice";
    private static final String TURTLE = "Turtle";
    private static final String GOLD_FISH = "Gold Fish";
    private static final String PROVIDED_CARE_CREATION_FORM = "care/createOrUpdateProvidedCareForm";
    private static final String INVALID_CARE = "cares/InvalidCare";

    @BeforeEach
    public void configureMock() throws NonCompatibleCaresException,UnfeasibleCareException{
        v=new Visit();
        v.setId(1);
		Pet p=new Pet();
		v.setPet(p);
		PetType pt=new PetType();
		pt.setName(TURTLE);
		pt.setId(9);
		p.setType(pt);
        
        Set<Care> incompatibleCares=new HashSet<Care>();
        care=new Care();
        care.setName(SHELL_POLISHING);
        Set<PetType> compatiblePetTypes=new HashSet<PetType>();
        compatiblePetTypes.add(pt);
        care.setCompatiblePetTypes(compatiblePetTypes);        
        setIncompatibleCares(care,incompatibleCares);

        care2=new Care();
        care2.setName(BRANQUIAL_CLEANING);
        Set<PetType> compatiblePetTypes2=new HashSet<PetType>();
        PetType pt2=new PetType();
        pt2.setId(10);
        pt2.setName(GOLD_FISH);
        compatiblePetTypes2.add(pt2);
        care2.setCompatiblePetTypes(compatiblePetTypes2);
        setIncompatibleCares(care2,incompatibleCares);

        Care care3=new Care();
        care3.setName(SACRIFICE);
        Set<PetType> compatiblePetTypes3=new HashSet<PetType>();
        compatiblePetTypes3.add(pt);
        compatiblePetTypes3.add(pt2);
        care3.setCompatiblePetTypes(compatiblePetTypes3);
        Set<Care> incompatibleCares2=new HashSet<Care>();
        incompatibleCares2.add(care);
        incompatibleCares2.add(care2);
        setIncompatibleCares(care3,incompatibleCares2);

        List<CareProvision> providedCares=new ArrayList<CareProvision>();
        CareProvision cp=new CareProvision();        
        cp.setVisit(v);
        cp.setCare(care2);
        providedCares.add(cp);
        when(careService.save(any(CareProvision.class))).thenReturn(null);
        when(petService.findVisitById(1)).thenReturn(v);
        when(careService.getCare(SHELL_POLISHING)).thenReturn(care);
        when(careService.getCare(BRANQUIAL_CLEANING)).thenReturn(care2);
        when(careService.getCare(SACRIFICE)).thenReturn(care3);
        when(careService.getAllCaresProvided()).thenReturn(providedCares);
    }

    @WithMockUser(value = "spring", authorities = {"admin"})
    @Test
    void test10()  throws Exception {
        testCareProvisionCreationControllerOK();                
        testCareProvisionCreationControllerUnfeasibleCareProvision();
        testCareProvisionCreationControllerNonCompatibleCareProvision();
        testAnnotations();
    }

	private void testCareProvisionCreationControllerNonCompatibleCareProvision() throws Exception {
        when(careService.save(any(CareProvision.class))).thenThrow(new NonCompatibleCaresException());
        mockMvc.perform(post("/visit/1/cares/create")
                            .with(csrf())
                            .param("care", BRANQUIAL_CLEANING)
                            .param("duration", "0.2"))
                .andExpect(status().isOk())				
				.andExpect(view().name(INVALID_CARE));
    }

    void testCareProvisionCreationControllerOK() throws Exception {
        mockMvc.perform(post("/visit/1/cares/create")
                            .with(csrf())
                            .param("care", SHELL_POLISHING)
                            .param("duration", "2.0"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
    }	
    
    void testCareProvisionCreationControllerUnfeasibleCareProvision() throws Exception {
        Mockito.when(careService.save(any(CareProvision.class))).thenThrow(new UnfeasibleCareException());
        mockMvc.perform(post("/visit/1/cares/create")
                            .with(csrf())
                            .param("care", BRANQUIAL_CLEANING)
                            .param("duration", "0.2"))
                .andExpect(status().isOk())				
				.andExpect(view().name(INVALID_CARE));
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

    public void testAnnotations() {
        Method handleInvalidCareException=null;
        try {
            handleInvalidCareException = ExceptionHandlingControllerAdvice.class.getDeclaredMethod("handleInvalidCareException",HttpServletRequest.class, Exception.class);
        } catch (NoSuchMethodException e) {
           fail("ExceptionHandlingControllerAdvice does not have a handleInvalidCareException method");
        } catch (SecurityException e) {
            fail("ExceptionHandlingControllerAdvice method is not accessible in FeedingService");
        }
        ExceptionHandler exceptionHandlerAnnotation=handleInvalidCareException.getAnnotation(ExceptionHandler.class);
        assertNotNull(exceptionHandlerAnnotation,"The method handleInvalidCareException is not annotated as ExceptionHandler");
        List<Class<? extends Throwable>> exceptionsWithRollbackFor=Arrays.asList(exceptionHandlerAnnotation.value());
        assertTrue(exceptionsWithRollbackFor.contains(NonCompatibleCaresException.class),"NonCompatibleCaresExceptions are not handled!");
        assertTrue(exceptionsWithRollbackFor.contains(UnfeasibleCareException.class),"UnfeasibleCareExceptions are not handled!");
    }
}
