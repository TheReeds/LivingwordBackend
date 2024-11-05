package living.word.livingword.repository;

import living.word.livingword.entity.DeviceToken;
import living.word.livingword.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByUser(User user);
    Optional<DeviceToken> findByToken(String token);
}