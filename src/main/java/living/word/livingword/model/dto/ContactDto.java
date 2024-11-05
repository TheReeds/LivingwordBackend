package living.word.livingword.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Long addedById;
    private String addedByName; // Nombre del usuario que añadió el contacto
    private String addedByLastname;
}
