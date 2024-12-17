package org.springframework.samples.petclinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.samples.petclinic.care.CareFormatter;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Test5 {

    @Autowired
    CareFormatter formatter;

    @Test
    public void test5(){
        testFormatterIsInjected();
        testFormatterObject2String();
        testFormatterString2Object();
        testFormatterString2ObjectNotFound();
    }
    
    public void testFormatterIsInjected(){
        assertNotNull(formatter);
    }
    
    public void testFormatterObject2String(){
        Care care=new Care();
        care.setName("Prueba");
        String result=formatter.print(care, null);
        assertEquals("Prueba",result, "The method print of the formatter is not working properly.");
    }
    
    public void testFormatterString2Object(){
        String name="Hair cut";
        Care care;
        try {
            care = formatter.parse(name, null);
            assertNotNull(care, "The method parse of the formatter is not working properly.");
            assertEquals(name, care.getName(), "The method parse of the formatter is not working properly.");
        } catch (ParseException e) {
            fail("The method parse of the formatter is not working properly.", e);
        }        
    }
    
    public void testFormatterString2ObjectNotFound(){
        String name="This is not a care";
        assertThrows(ParseException.class, () -> formatter.parse(name, null), "The method parse of the formatter is not working properly.");
    }
}
