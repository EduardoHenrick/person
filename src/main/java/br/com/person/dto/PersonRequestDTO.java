package br.com.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonRequestDTO(@NotBlank(message = "O nome não pode ser vazio") String name,
                               @NotNull(message = "A idade deve ser informada") Integer age) {


}
