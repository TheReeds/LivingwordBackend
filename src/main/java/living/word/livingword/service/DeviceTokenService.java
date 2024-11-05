package living.word.livingword.service;

import jakarta.transaction.Transactional;
import living.word.livingword.entity.DeviceToken;
import living.word.livingword.entity.User;
import living.word.livingword.repository.DeviceTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceTokenService {

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    // Añadir un nuevo token
    @Transactional
    public void addDeviceToken(User user, String token) {
        // Verificar si el token ya existe para evitar duplicados
        Optional<DeviceToken> existingToken = deviceTokenRepository.findByToken(token);
        if (existingToken.isEmpty()) {
            DeviceToken deviceToken = new DeviceToken();
            deviceToken.setToken(token);
            deviceToken.setUser(user);
            deviceTokenRepository.save(deviceToken);
        }
    }

    // Eliminar un token existente
    @Transactional
    public void removeDeviceToken(String token) {
        deviceTokenRepository.findByToken(token).ifPresent(deviceTokenRepository::delete);
    }
}