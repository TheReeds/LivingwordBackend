package living.word.livingword.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsletterDto {
    private Long id;
    private String title;
    private String newsletterUrl;
    private LocalDateTime publicationDate;
    private Long uploadedById;
    private String uploadedByFirstName;
    private String uploadedBySecondName;
}
