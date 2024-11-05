package living.word.livingword.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrayerSupportDTO {
    private Long id;
    private String username;  // Usuario que oró
    private LocalDateTime supportDate;
}