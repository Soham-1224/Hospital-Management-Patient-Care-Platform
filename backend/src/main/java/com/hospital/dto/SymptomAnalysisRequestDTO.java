package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymptomAnalysisRequestDTO {

    @NotBlank(message = "Symptoms cannot be empty")

    @Size(max = 3000,
            message = "Symptoms cannot exceed 3000 characters")

    private String symptoms;
}
