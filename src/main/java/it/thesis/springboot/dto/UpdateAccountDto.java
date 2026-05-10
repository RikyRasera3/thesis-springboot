package it.thesis.springboot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
public class UpdateAccountDto {
    private Optional<String> name;
    private Optional<String> surname;
    private Optional<String> email;
    private Optional<String> phone;
    private Optional<LocalDate> dateOfBirth;
    private Optional<@NotEmpty @Size(min = 1) List<Long>> roleIds;
}