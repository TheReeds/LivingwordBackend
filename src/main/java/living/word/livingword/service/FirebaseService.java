package living.word.livingword.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;

import living.word.livingword.entity.DeviceToken;
import living.word.livingword.repository.DeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseService {
    private final DeviceTokenRepository deviceTokenRepository;

    @Autowired
    public FirebaseService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public BatchResponse sendNotificationToAllUsers(String title, String body, Map<String, String> data, String imageUrl) {
        List<String> tokens = deviceTokenRepository.findAll().stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());
    
        if (tokens.isEmpty()) {
            log.warn("No device tokens found for notification");
            return null;
        }
    
        // Directly pass parameters to sendMulticastNotification
        return sendMulticastNotification(tokens, title, body, data, imageUrl);
    }    

    public BatchResponse sendMulticastNotification(List<String> tokens, String title, String body, Map<String, String> data, String imageUrl) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(imageUrl)
                            .build())
                    .putAllData(data != null ? data : new HashMap<>())
                    .addAllTokens(tokens)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Successfully sent message to {} recipients", response.getSuccessCount());
            
            handleFailedTokens(tokens, response.getResponses());
                
            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Error sending Firebase notification", e);
            throw new RuntimeException("Error sending Firebase notification", e);
        }
    }

    private void handleFailedTokens(List<String> tokens, List<SendResponse> responses) {
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                String failedToken = tokens.get(i);
                MessagingErrorCode errorCode = responses.get(i).getException().getMessagingErrorCode();
                
                if (errorCode == MessagingErrorCode.UNREGISTERED || 
                    errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                    log.warn("Removing invalid token: {}", failedToken);
                    deviceTokenRepository.findByToken(failedToken)
                            .ifPresent(deviceTokenRepository::delete);
                }
            }
        }
    }
}