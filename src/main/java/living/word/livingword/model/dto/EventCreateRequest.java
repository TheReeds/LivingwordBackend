package living.word.livingword.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

@Data
public class EventCreateRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private MultipartFile imageFile; 
}