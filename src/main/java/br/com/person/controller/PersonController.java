package br.com.person.controller;

import br.com.person.dto.PersonRequestDTO;
import br.com.person.dto.PersonResponseDTO;
import br.com.person.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
@Tag(name = "Pessoas", description = "APIs relacionadas ao gerenciamento de pessoas")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar uma nova pessoa", description = "Cria uma nova pessoa com os dados fornecidos")
    @PostMapping
    public ResponseEntity<PersonResponseDTO> create(@RequestBody @Valid @NotNull(message = "o corpo da requisição não pode ser nulo") PersonRequestDTO person) {
        PersonResponseDTO save = service.save(person);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(save);
    }

    @Operation(summary = "Listar todas as pessoas", description = "Retorna uma lista de todas as pessoas cadastradas")
    @GetMapping
    public List<PersonResponseDTO> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Buscar pessoa por ID", description = "Retorna os dados de uma pessoa específica pelo seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar pessoa por ID", description = "Atualiza os dados de uma pessoa específica pelo seu ID")
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> update(@PathVariable Long id, @RequestBody PersonRequestDTO personRequestDTO) {
        return service.update(id, personRequestDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar pessoa por ID", description = "Remove uma pessoa específica pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.findById(id)
                .map(existingPerson -> {
                    service.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
