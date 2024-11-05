package living.word.livingword.model.dto;

import org.threeten.bp.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDto {
    private Long id;
    private String title;
    private String youtubeUrl;
    private String uploadedByUsername;
    private LocalDateTime uploadedDate;
}
