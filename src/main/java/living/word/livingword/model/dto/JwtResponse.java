package living.word.livingword.model.dto;


import lombok.Data;

import java.util.Set;

@Data
public class JwtResponse {
    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;
    private Set<String> permissions;
    private String ministry; // Nuevo campo

    public JwtResponse(String token, Long id, String name, String email, String role, Set<String> permissions, String ministry) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.permissions = permissions;
        this.ministry = ministry;
    }
}