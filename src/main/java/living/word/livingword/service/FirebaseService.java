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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import living.word.livingword.entity.DeviceToken;
import living.word.livingword.repository.DeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseService {
    private final DeviceTokenRepository deviceTokenRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

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

        // Enviar notificaciones en lotes
        int batchSize = 500; // Tamaño del lote
        BatchResponse finalResponse = null;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            int end = Math.min(i + batchSize, tokens.size());
            List<String> batchTokens = tokens.subList(i, end);
            BatchResponse response = sendMulticastNotification(batchTokens, title, body, data, imageUrl);
            if (finalResponse == null) {
                finalResponse = response;
            } else {
                // Combinar respuestas si es necesario
            }
        }
        return finalResponse;
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

            log.info("Sending notification to the following tokens: {}", tokens);

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Successfully sent message to {} recipients", response.getSuccessCount());

            handleFailedTokens(tokens, response.getResponses());

            // Log details of each successful and failed token
            logTokenStatuses(tokens, response.getResponses());

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
                    log.warn("Removing invalid or inactive token: {}", failedToken);
                    deviceTokenRepository.findByToken(failedToken)
                            .ifPresent(deviceTokenRepository::delete);
                }
            }
        }
    }

    private void logTokenStatuses(List<String> tokens, List<SendResponse> responses) {
        for (int i = 0; i < responses.size(); i++) {
            String token = tokens.get(i);
            SendResponse response = responses.get(i);

            if (response.isSuccessful()) {
                log.info("Successfully sent notification to token: {}", token);
            } else {
                MessagingErrorCode errorCode = response.getException().getMessagingErrorCode();
                String errorMessage = response.getException().getMessage();

                log.error("Failed to send notification to token: {}. Error Code: {}, Message: {}", 
                          token, errorCode, errorMessage);
            }
        }
    }
}
