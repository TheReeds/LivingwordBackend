package living.word.livingword.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class SermonNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String sermonurl;
    private LocalDateTime date;

    @ManyToOne
    private User uploadedBy;
}
