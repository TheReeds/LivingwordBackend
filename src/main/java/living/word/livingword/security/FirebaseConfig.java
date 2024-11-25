package living.word.livingword.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                log.info("Inicializando Firebase usando variable de entorno");

                // Leer el contenido del archivo JSON desde la variable de entorno
                String firebaseConfig = System.getenv("FIREBASE_CONFIG");
                if (firebaseConfig == null || firebaseConfig.isEmpty()) {
                    throw new IllegalStateException("La variable de entorno FIREBASE_CONFIG no está configurada");
                }

                // Convertir el JSON en un flujo de entrada
                ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes());

                // Configurar Firebase
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase ha sido inicializado exitosamente desde la variable de entorno");
            }
        } catch (IOException e) {
            log.error("Error al inicializar Firebase", e);
            throw new RuntimeException("No se pudo inicializar Firebase", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}