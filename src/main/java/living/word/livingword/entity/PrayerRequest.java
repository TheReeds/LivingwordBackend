package living.word.livingword.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class PrayerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private LocalDateTime date;
    private int prayerCount = 0;  // Number of users prayers

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;  // User for prayer request

    @OneToMany(mappedBy = "prayerRequest", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PrayerSupport> supports; // Users for prayer support

    // increment count
    public void incrementPrayerCount() {
        this.prayerCount++;
    }
}