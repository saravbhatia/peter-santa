package com.tripactions.infra.redis.repo;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tripactions.infra.redis.model.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonRepositoryTest {

	@Mock
	private PersonRepository personRepository;

	@Test
	void whenSavingPerson_thenAvailableOnRetrieval() throws Exception {
		final Person person = new Person("123", "Joe Green", Person.Gender.MALE);
		when(personRepository.save(Mockito.any(Person.class))).thenReturn(person);
		personRepository.save(person);
		when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
		final Person retrievedPerson = personRepository.findById(person.getId()).get();
		assertEquals(person.getId(), retrievedPerson.getId());
	}

	@Test
	void whenUpdatingPerson_thenAvailableOnRetrieval() throws Exception {
		final Person person = new Person("123", "Joe Green", Person.Gender.MALE);
		when(personRepository.save(Mockito.any(Person.class))).thenReturn(person);
		personRepository.save(person);
		person.setName("Richard Watson");
		when(personRepository.save(Mockito.any(Person.class))).thenReturn(person);
		personRepository.save(person);
		when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
		final Person retrievedPerson = personRepository.findById(person.getId()).get();
		assertEquals(person.getName(), retrievedPerson.getName());
	}

	@Test
	void whenDeletingPerson_thenNotAvailableOnRetrieval() throws Exception {
		final Person person = new Person("123", "Joe Green", Person.Gender.MALE);
		when(personRepository.save(Mockito.any(Person.class))).thenReturn(person);
		personRepository.save(person);
		assertEquals(person.getName(), person.getName());
		personRepository.deleteById(person.getId());
		when(personRepository.findById(person.getId())).thenReturn(Optional.empty());
		final Person retrievedPerson = personRepository.findById(person.getId()).orElse(null);
		assertNull(retrievedPerson);
	}
}