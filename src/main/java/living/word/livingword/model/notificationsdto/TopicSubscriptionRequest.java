package living.word.livingword.model.notificationsdto;

import java.util.List;

import lombok.Data;

@Data
public class TopicSubscriptionRequest {
    private String topic;
    private List<String> tokens;
}