package living.word.livingword.service;

import com.google.firebase.messaging.*;
import living.word.livingword.entity.*;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.DeviceTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    private final FirebaseService firebaseService;
    private final DeviceTokenRepository deviceTokenRepository;
    private final AppUserRepository userRepository;

    @Autowired
    public NotificationService(FirebaseService firebaseService,
                             DeviceTokenRepository deviceTokenRepository,
                             AppUserRepository userRepository) {
        this.firebaseService = firebaseService;
        this.deviceTokenRepository = deviceTokenRepository;
        this.userRepository = userRepository;
    }

    // Newsletter notifications
    public void sendNewsletterNotification(Newsletter newsletter) {
        String title = "New newsletter available";
        String body = newsletter.getTitle();
        String imageUrl = "https://i.ibb.co/7jxkCRP/JOJO.png";

        Map<String, String> data = new HashMap<>();
        data.put("type", "newsletter");
        data.put("newsletterId", newsletter.getId().toString());
        data.put("action", "open_newsletter");

        try {
            BatchResponse response = firebaseService.sendNotificationToAllUsers(title, body, data, imageUrl);
            log.info("Newsletter notification sent successfully to {} recipients", 
                    response != null ? response.getSuccessCount() : 0);
        } catch (Exception e) {
            log.error("Error sending newsletter notification", e);
        }
    }

    // Sermon notifications
    public void sendSermonStartNotification(Sermon sermon) {
        String title = "¡El culto está comenzando!";
        String body = String.format("El culto '%s' está por comenzar", sermon.getTitle());
        String imageUrl = "";
        Map<String, String> data = new HashMap<>();
        data.put("type", "sermon_start");
        data.put("sermonId", sermon.getId().toString());
        data.put("action", "open_sermon");

        try {
            BatchResponse response = firebaseService.sendNotificationToAllUsers(title, body, data, imageUrl);
            log.info("Sermon start notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending sermon start notification", e);
        }
    }

    public void sendSermonEndNotification(Sermon sermon) {
        String title = "Finalización del Culto";
        String body = "¿Nos ayudas con tu feedback sobre el culto de hoy?";
        String imageUrl = "";
        Map<String, String> data = new HashMap<>();
        data.put("type", "sermon_end");
        data.put("sermonId", sermon.getId().toString());
        data.put("action", "sermon_feedback");

        try {
            BatchResponse response = firebaseService.sendNotificationToAllUsers(title, body, data, imageUrl);
            log.info("Sermon end notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending sermon end notification", e);
        }
    }

    // Prayer request notifications
    public void sendPrayerRequestNotification(PrayerRequest prayerRequest, User prayingUser) {
        User requestOwner = prayerRequest.getUser();
        String title = "Alguien está orando por ti";
        String body = String.format("%s está orando por tu petición", prayingUser.getName());
        String imageUrl = "";
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "prayer_request");
        data.put("prayerRequestId", prayerRequest.getId().toString());
        data.put("action", "open_prayer_request");

        List<String> tokens = deviceTokenRepository.findByUser(requestOwner)
                .stream()
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        if (!tokens.isEmpty()) {
            try {
                firebaseService.sendMulticastNotification(tokens, title, body, data, imageUrl);
                log.info("Prayer request notification sent to user {}", requestOwner.getId());
            } catch (Exception e) {
                log.error("Error sending prayer request notification", e);
            }
        }
    }

    // Event notifications
    public void sendEventNotification(Event event) {
        String title = "New Event: " + event.getTitle();  // Título del evento
        String body = event.getDescription();  // Descripción del evento
        String imageUrl = event.getImageUrl() != null && !event.getImageUrl().isEmpty() //imagen del evento
        ? "http://localhost:6500/events/images/" + event.getImageUrl() 
        : "https://www.shutterstock.com/image-vector/events-colorful-typography-banner-260nw-1356206768.jpg";
    
        Map<String, String> data = new HashMap<>();
        data.put("type", "event");
        data.put("eventId", event.getId().toString());
        data.put("action", "open_event");  // Acción para abrir el evento
    
        try {
            BatchResponse response = firebaseService.sendNotificationToAllUsers(title, body, data, imageUrl);
            log.info("Event notification sent successfully to {} recipients", 
                     response != null ? response.getSuccessCount() : 0);
        } catch (Exception e) {
            log.error("Error sending event notification", e);
        }
    }

    // Absence notifications to administrators
    /*public void notifyAdminAboutAbsence(User user, Sermon sermon) {
        List<User> admins = userRepository.findByRole("ADMINISTRATOR");
        
        String title = "Reporte de Ausencia";
        String body = String.format("%s no asistió al culto: %s", 
                                  user.getName(), sermon.getTitle());
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "absence");
        data.put("userId", user.getId().toString());
        data.put("sermonId", sermon.getId().toString());
        data.put("action", "view_absence");

        List<String> adminTokens = admins.stream()
                .flatMap(admin -> deviceTokenRepository.findByUser(admin).stream())
                .map(DeviceToken::getToken)
                .collect(Collectors.toList());

        if (!adminTokens.isEmpty()) {
            try {
                firebaseService.sendMulticastNotification(adminTokens, title, body, data);
                log.info("Admin absence notification sent successfully");
            } catch (Exception e) {
                log.error("Error sending admin absence notification", e);
            }
        }
    }*/

    // Utility methods
    private String formatEventDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
}