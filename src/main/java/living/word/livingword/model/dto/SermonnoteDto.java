package living.word.livingword.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SermonnoteDto {
    private Long id;
    private String title;
    private String sermonurl;
    private LocalDateTime date;
    private Long addedById;
    private String addedByName; // Nombre del usuario que añadió el contacto
    private String addedByLastname;
}
