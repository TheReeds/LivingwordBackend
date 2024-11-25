package living.word.livingword.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String location;
    private LocalDateTime eventDate;
    private Long addedById;
    private String createdByUsername; 
    private String createdByLastname;
    private String createdByMinistry;
}