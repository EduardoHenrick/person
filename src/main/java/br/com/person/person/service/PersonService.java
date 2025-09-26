package br.com.person.person.service;

import br.com.person.person.exception.AgePersonValidationException;
import br.com.person.person.dto.PersonRequestDTO;
import br.com.person.person.dto.PersonResponseDTO;
import br.com.person.person.entity.Person;
import br.com.person.person.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public PersonResponseDTO save(PersonRequestDTO personRequestDTO) {
        if (personRequestDTO.age() < 16) {
            throw new AgePersonValidationException("A pessoa deve ter 16 anos ou mais para se cadastrar.");
        }
        Person person = new Person(personRequestDTO.name(), personRequestDTO.age());
        Person savedPerson = repository.save(person);
        return new PersonResponseDTO(savedPerson.getName(), savedPerson.getAge());
    }

    public List<PersonResponseDTO> findAll() {
        List<Person> persons = repository.findAll();
        List<PersonResponseDTO> personDTOs = new ArrayList<>();

        for (Person person : persons) {
            personDTOs.add(new PersonResponseDTO(person.getName(), person.getAge()));
        }
        return personDTOs;
    }

    public Optional<PersonResponseDTO> findById(Long id) {
        return repository.findById(id)
                .map(person -> new PersonResponseDTO(person.getName(), person.getAge()));
    }

    public Optional<PersonResponseDTO> update(Long id, PersonRequestDTO personRequestDTO) {
        return repository.findById(id)
                .map(existingPerson -> {
                    if (personRequestDTO.age() < 16) {
                        throw new AgePersonValidationException("A idade não pode ser alterada para menos de 16 anos.");
                    }
                    existingPerson.setName(personRequestDTO.name());
                    existingPerson.setAge(personRequestDTO.age());
                    Person updatedPerson = repository.save(existingPerson);
                    return new PersonResponseDTO(updatedPerson.getName(), updatedPerson.getAge());
                });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

}