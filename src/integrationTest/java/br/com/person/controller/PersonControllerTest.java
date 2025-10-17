package br.com.person.controller;

import br.com.person.dto.PersonRequestDTO;
import br.com.person.dto.PersonResponseDTO;
import br.com.person.entity.Person;
import br.com.person.repository.TestH2Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {

    @LocalServerPort
    private int port;

    private String baseURL = "http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository h2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setup(){
        baseURL += ":".concat(String.valueOf(port)).concat("/persons");
    }

    @Test
    public void mustCreatePerson() {
        PersonRequestDTO personRequestDTO = new PersonRequestDTO("Eduardo", 16);
        ResponseEntity<PersonResponseDTO> response = restTemplate.postForEntity(baseURL, personRequestDTO, PersonResponseDTO.class);

        PersonResponseDTO personResponseDTO = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(personResponseDTO);
        assertEquals("Eduardo", personResponseDTO.name());
        assertEquals(16, personResponseDTO.age());

        Optional<Person> optionalPerson = h2Repository.findById(1L);

        assertTrue(optionalPerson.isPresent());
        assertEquals("Eduardo", optionalPerson.get().getName());
        assertEquals(16, optionalPerson.get().getAge());
    }

}
