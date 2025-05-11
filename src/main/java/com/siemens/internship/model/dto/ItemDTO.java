package com.siemens.internship.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    @NotBlank(message = "Name is mandatory")
    @NotNull(message = "Name is mandatory")
    @NotEmpty
    private String name;
    @NotBlank
    private String description;
    private String status;
    @NotEmpty
    @Email
    private String email;
}
