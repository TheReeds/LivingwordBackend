package living.word.livingword.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ejemplo: "NEWSLETTER_READ", "NEWSLETTER_WRITE"

    public Permission() {}

    // Constructor con parámetros
    public Permission(String name) {
        this.name = name;
    }
}