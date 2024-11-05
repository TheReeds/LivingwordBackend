package living.word.livingword.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PrayerRequestDTO {
    private Long id;
    private String description;
    private LocalDateTime date;
    private int prayerCount;
    private String username;  // Usuario que realizó el pedido de oración
}