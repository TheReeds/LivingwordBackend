package living.word.livingword.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinistryDto {
    private Long id;
    private String name;
    private String description;
    private List<UserDTO> leaders; // Solo líderes del ministerio
}