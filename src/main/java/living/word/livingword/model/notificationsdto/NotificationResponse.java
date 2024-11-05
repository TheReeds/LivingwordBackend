package living.word.livingword.model.notificationsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private boolean success;
    private int recipientCount;
    private String message;
}
