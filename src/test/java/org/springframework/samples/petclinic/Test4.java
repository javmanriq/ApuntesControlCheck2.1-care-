package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.care.CareProvision;
import org.springframework.samples.petclinic.care.CareProvisionRepository;
import org.springframework.samples.petclinic.care.CareService;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class Test4 {

    @Autowired(required = false)
    CareService fs;

    @MockBean
    CareProvisionRepository cr;
    
    @Test public void test4(){
        careServiceIsInjected();
        careServiceCanGetCares();
    }
    
    public void careServiceIsInjected() {
        assertNotNull(fs,"CareService was not injected by spring");       
    }
    
    public void careServiceCanGetCares(){
        when(cr.findAll()).thenReturn(new ArrayList<CareProvision>());
        assertNotNull(fs,"CareService was not injected by spring");
        List<CareProvision> cares=fs.getAllCaresProvided();
        assertNotNull(cares,"The list of careProvisions found by the service was null");
        verify(cr,times(1)).findAll();       
        
    }
}
