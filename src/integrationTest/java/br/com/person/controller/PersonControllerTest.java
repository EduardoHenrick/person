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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {





    //TESTES NAO ESTAO PASSANDO




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
    @Sql(statements = {
            "DELETE FROM PERSON",
            "ALTER TABLE PERSON ALTER COLUMN id RESTART WITH 1"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    @Test
    @Sql(statements = "INSERT INTO PERSON (name, age) VALUES ('Eduardo', 16);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PERSON WHERE name='Eduardo'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM PERSON",
            "ALTER TABLE PERSON ALTER COLUMN id RESTART WITH 1"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void mustGetAllPerson() {
        ResponseEntity<PersonResponseDTO[]> response = restTemplate.getForEntity(baseURL, PersonResponseDTO[].class);
        PersonResponseDTO[] personResponseDTOS = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(personResponseDTOS);
        assertEquals(1, personResponseDTOS.length);
        assertEquals("Eduardo", personResponseDTOS[0].name());
        assertEquals(16, personResponseDTOS[0].age());
        assertEquals(1, h2Repository.findAll().size());
    }

    @Test
    @Sql(statements = "INSERT INTO PERSON (name, age) VALUES ('Eduardo', 16);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PERSON", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM PERSON",
            "ALTER TABLE PERSON ALTER COLUMN id RESTART WITH 1"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void mustGetPersonById() {
        ResponseEntity<PersonResponseDTO> response = restTemplate.getForEntity(baseURL + "/{id}", PersonResponseDTO.class, 1L);
        PersonResponseDTO personResponseDTO = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(personResponseDTO);
        assertEquals("Eduardo", personResponseDTO.name());
        assertEquals(16, personResponseDTO.age());
    }

    @Test
    @Sql(statements = "INSERT INTO PERSON (name, age) VALUES ('Eduardo', 16);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM PERSON", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM PERSON",
            "ALTER TABLE PERSON ALTER COLUMN id RESTART WITH 1"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void mustUpdatePerson() {
        PersonRequestDTO personRequestDTO = new PersonRequestDTO("Eduardo Henrick", 17);
        restTemplate.put(baseURL + "/{id}", personRequestDTO, 1L);

        Optional<Person> optionalPerson = h2Repository.findById(1L);

        assertTrue(optionalPerson.isPresent());
        assertEquals("Eduardo Henrick", optionalPerson.get().getName());
        assertEquals(17, optionalPerson.get().getAge());
    }

    @Test
    @Sql(statements = "INSERT INTO PERSON (name, age) VALUES ('Eduardo', 16);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM PERSON",
            "ALTER TABLE PERSON ALTER COLUMN id RESTART WITH 1"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void mustDeletePerson() {
        int recordCount = h2Repository.findAll().size();
        assertEquals(1, recordCount);
        restTemplate.delete(baseURL + "/{id}", 1L);
        assertEquals(0, h2Repository.findAll().size());
    }
}
