package org.springframework.samples.petclinic.pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.care.Care;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class PetFormatter implements Formatter<Pet> {

	private final PetService petService;

	@Autowired
	public PetFormatter(PetService petService) {
		this.petService = petService;
	}
	
	@Override
	public String print(Pet pet, Locale locale) {
		return pet.getName();
	}

	@Override
	public Pet parse(String text, Locale locale) throws ParseException {
		Pet pet = petService.getPetByName(text);
    	if(pet != null) {
    		return pet;
    	}else {
    		throw new ParseException("Pet not found: " + text, 0);
    	}
	}

}
