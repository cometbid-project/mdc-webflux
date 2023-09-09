/**
 * 
 */
package com.ndportmann.mdc_webflux.service.model;

import org.springframework.stereotype.Component;

import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.util.IdGenerator;

import lombok.RequiredArgsConstructor;

/**
 * @author Gbenga
 *
 */
@Component
@RequiredArgsConstructor
public class PersonMapper {

	private final ModelMapper modelMapper;
	private final IdGenerator idGenerator;

	private static final Random random = new Random();

	/*
	public Person toEntity(PersonModel personModel) {

		Person entity = modelMapper.map(personModel, Person.class);
		return entity.setId(random.nextLong());
	}
	
	public Person toUpdate(PersonModel personModel, Long id) {

		Person entity = modelMapper.map(personModel, Person.class);
		return entity.setId(id);
	}

	public PersonModel toModel(Person sourceEntity) {

		return modelMapper.map(sourceEntity, PersonModel.class);
	}
	*/
}
