package living.word.livingword.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import org.threeten.bp.LocalDateTime;

@Entity
@Data
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String youtubeUrl;
    private LocalDate publicationDate;
    private LocalDateTime uploadedDate;
    @ManyToOne
    private User uploadedBy;
}
