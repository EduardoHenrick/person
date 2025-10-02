package br.com.person.person;

import br.com.person.person.entity.Person;
import br.com.person.person.exception.AgePersonValidationException;
import br.com.person.person.repository.PersonRepository;
import br.com.person.person.service.PersonService;
import net.bytebuddy.matcher.ElementMatcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static net.bytebuddy.matcher.ElementMatchers.is;
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

    @BeforeEach
    public void setup() {
        person = new Person("Eduardo", 16);
    }

    @Test
    void deveSalvarPessoaComIdadeValida() {
        when(repository.save(person)).thenReturn(person);

        Person saved = service.save(person);

        assertThat(saved, notNullValue());
        assertThat(saved.getName(), is("Eduardo"));
        assertThat(saved.getAge(), is(16));

        verify(repository).save(person);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void naoDeveSalvarPessoaQuandoIdadeInvalida() {
        person.setAge(10);

        AgePersonValidationException e = assertThrows(AgePersonValidationException.class, () -> service.save(person));

        MatcherAssert.assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("A idade mínima para cadastro é 16 anos."));

        verifyNoInteractions(repository);
    }

}
