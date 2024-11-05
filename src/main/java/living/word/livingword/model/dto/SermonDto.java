package living.word.livingword.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SermonDto {
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalDateTime endTime;

    private String videoLink;

    private String summary;

    private boolean active;
}