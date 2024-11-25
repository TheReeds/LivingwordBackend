package living.word.livingword.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String role;
    private Set<String> permissions;
    private String ministry;
    private String photoUrl;

}