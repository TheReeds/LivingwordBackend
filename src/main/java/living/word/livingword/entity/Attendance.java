package living.word.livingword.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import living.word.livingword.model.AttendanceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;  // Fecha y hora del culto

    private boolean attended;    // Si asistió o no
    private Integer rating;      // Calificación del culto, si asistió (1-5 estrellas)
    private String feedback;     // Comentarios adicionales
    private AttendanceStatus status; // Estado de la respuesta (No Respondió, Asistió, No Asistió)

    @ManyToOne
    @JsonBackReference
    private User user;

    @ManyToOne
    @JsonBackReference
    private Sermon sermon; // Asociado al resumen del sermón

    private String youtubeLink;  // Enlace al video de YouTube del culto

    private LocalDateTime attendedAt;
}
