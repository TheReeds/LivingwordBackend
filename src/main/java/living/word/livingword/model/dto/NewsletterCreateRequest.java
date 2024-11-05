package living.word.livingword.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsletterCreateRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;
}