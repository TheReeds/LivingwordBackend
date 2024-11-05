    package living.word.livingword.entity;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.Data;

    import java.time.LocalDateTime;
    import java.util.List;

    @Entity
    @Data
    public class Sermon {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String videoLink;
        private String summary;

        private boolean active; // Indica si el sermón está actualmente activo

        @ManyToOne
        private User createdBy;

        @OneToMany(mappedBy = "sermon", cascade = CascadeType.ALL)
        @JsonManagedReference
        private List<Attendance> attendanceRecords;
    }