package living.word.livingword.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Newsletter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String newsletterUrl;
    private LocalDateTime publicationDate;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;
}
