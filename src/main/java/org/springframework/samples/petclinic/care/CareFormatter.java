package org.springframework.samples.petclinic.care;

import java.text.ParseException;
import java.util.Locale;

import lombok.AllArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

// Test 5
@Component
@AllArgsConstructor
public class CareFormatter implements Formatter<Care>{

    private final CareService careService;

    @Override
    public String print(Care object, Locale locale) {
        return object.getName();
    }

    @Override
    public Care parse(String text, Locale locale) throws ParseException {
        Care care = careService.getCare(text);
        if (care == null)
            throw new ParseException("Care not found: " + text, 0);
        return care;
    }
    
}