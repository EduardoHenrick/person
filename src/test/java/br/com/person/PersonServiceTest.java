package br.com.person;

import br.com.person.dto.PersonResponseDTO;
import br.com.person.entity.Person;
import br.com.person.exception.AgePersonValidationException;
import br.com.person.repository.PersonRepository;
import br.com.person.service.PersonService;
import br.com.person.dto.PersonRequestDTO;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonService service;

    Person person;
    private PersonRequestDTO validUpdateDTO;
    private PersonRequestDTO invalidUpdateDTO;

    @BeforeEach
    public void setup() {
        person = new Person("Eduardo", 16);
        person.setId(1L);
        validUpdateDTO = new PersonRequestDTO("Eduardo", 16);
        invalidUpdateDTO = new PersonRequestDTO("Eduardo", 14);
    }

    @Test
    @DisplayName("Deve salvar pessoa com idade válida")
    void shouldSavePersonWithValidAge() {
        when(repository.save(person)).thenReturn(person);

        PersonResponseDTO saved = service.save(validUpdateDTO);

        assertThat(saved, notNullValue());
        assertThat(saved.name(), is("Eduardo"));
        assertThat(saved.age(), is(16));

        verify(repository).save(person);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void naoDeveSalvarPessoaQuandoIdadeInvalida() {

        AgePersonValidationException e = assertThrows(AgePersonValidationException.class, () -> service.save(invalidUpdateDTO));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("A pessoa deve ter 16 anos ou mais para se cadastrar."));

        verifyNoInteractions(repository);
    }

    @Test
    void deveRetornarTodasPessoas() {
        when(repository.findAll()).thenReturn(List.of(person));

        List<PersonResponseDTO> persons = service.findAll();

        assertThat(persons, notNullValue());
        assertThat(persons.size(), is(1));
        assertThat(persons.getFirst().name(), is("Eduardo"));
        assertThat(persons.getFirst().age(), is(16));

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveRetornarPessoaPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        Optional<PersonResponseDTO> found = service.findById(1L);

        assertThat(found.isPresent(), is(true));
        assertThat(found.get().name(), is("Eduardo"));
        assertThat(found.get().age(), is(16));

        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveAtualizarPessoaComIdadeValida() {
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<PersonResponseDTO> updated = service.update(1L, validUpdateDTO);

        assertThat(updated.isPresent(), is(true));
        assertThat(updated.get().name(), is("Eduardo"));
        assertThat(updated.get().age(), is(16));

        verify(repository).findById(1L);
        verify(repository).save(any(Person.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void naoDeveAtualizarPessoaQuandoIdadeInvalida() {
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        AgePersonValidationException e = assertThrows(AgePersonValidationException.class, () -> service.update(1L, invalidUpdateDTO));

        MatcherAssert.assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("A idade não pode ser alterada para menos de 16 anos."));

        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deveDeletarPessoaPorId() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
        verifyNoMoreInteractions(repository);
    }



}
