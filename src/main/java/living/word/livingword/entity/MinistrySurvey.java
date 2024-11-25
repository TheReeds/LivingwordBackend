package living.word.livingword.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MinistrySurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    private Ministry ministry;  

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  

    @Enumerated(EnumType.STRING)
    private ParticipationResponse response;  // Respuesta del usuario ("YES", "NO", "MAYBE")

    public enum ParticipationResponse {
        YES, NO, MAYBE
    }
}