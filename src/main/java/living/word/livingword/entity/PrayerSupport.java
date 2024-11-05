package living.word.livingword.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PrayerSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private User user;  // User Support
    @ManyToOne
    @JsonBackReference
    private PrayerRequest prayerRequest;  // Prayer associate
    private LocalDateTime supportDate;
}