package org.springframework.samples.petclinic.care;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class CareController {

    @GetMapping("/care/create")
    public String createCare(){
        return "care/createOrUpdateProvidedCareForm";
    }

    @PostMapping("/care/create")
    public String createCare(BindingResult result){
        if (result.hasErrors()) {
            return "cares/InvalidCare.jsp";
        }
        return "care/createOrUpdateProvidedCareForm";
    }
    
}
