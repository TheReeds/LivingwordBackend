package living.word.livingword.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AttendanceFeedbackRequest {
    private boolean attended;

    @Min(1)
    @Max(5)
    private Integer rating; // 1-5 estrellas, opcional si asistió

    private String feedback; // Comentario opcional si asistió
}