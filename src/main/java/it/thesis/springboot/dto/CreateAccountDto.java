package it.thesis.springboot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateAccountDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    private String email;

    @NotEmpty
    private String phone;

    @NotNull
    private LocalDate dateOfBirth;

    @NotEmpty
    @Size(min = 1)
    private List<Long> roleIds;
}