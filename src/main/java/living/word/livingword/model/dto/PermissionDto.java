package living.word.livingword.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionDto {
    private Long id;

    @NotBlank(message = "Permit name is required")
    private String name;
}