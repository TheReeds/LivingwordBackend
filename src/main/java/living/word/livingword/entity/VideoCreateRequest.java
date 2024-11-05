package living.word.livingword.entity;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "YouTube URL is required")
    private String youtubeUrl;
    @NotNull(message = "Publication date is required")
    private LocalDate publicationDate;

}
