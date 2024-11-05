package living.word.livingword.model.notificationsdto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String body;
    private Map<String, String> data;
    private List<String> tokens;
    private String topic;
}