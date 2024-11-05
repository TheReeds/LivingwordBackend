package living.word.livingword.repository;

import living.word.livingword.entity.PrayerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrayerRequestRepository extends JpaRepository<PrayerRequest, Long> {
    List<PrayerRequest> findByDate(LocalDate date);
    Page<PrayerRequest> findByDate(LocalDate date, Pageable pageable);
}