package br.com.person.repository;

import br.com.person.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestH2Repository extends JpaRepository<Person, Long> {



}
