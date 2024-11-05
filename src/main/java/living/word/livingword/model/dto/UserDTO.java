package living.word.livingword.model.dto;

import living.word.livingword.entity.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
}