package living.word.livingword.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class RoleDto {
    private Long id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    @NotNull(message = "El nivel del rol es obligatorio")
    private Integer level;

    private Set<String> permissions; // Nombres de permisos, por ejemplo: "NEWSLETTER_READ"
}