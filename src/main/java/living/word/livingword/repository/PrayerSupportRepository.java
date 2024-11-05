package living.word.livingword.repository;

import living.word.livingword.entity.PrayerRequest;
import living.word.livingword.entity.PrayerSupport;
import living.word.livingword.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrayerSupportRepository extends JpaRepository<PrayerSupport, Long> {
    boolean existsByUserAndPrayerRequest(User user, PrayerRequest prayerRequest);

    void deleteByPrayerRequest(PrayerRequest prayerRequest);

    List<PrayerSupport> findByPrayerRequest(PrayerRequest prayerRequest);
}