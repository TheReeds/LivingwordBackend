package living.word.livingword.repository;

import living.word.livingword.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SermonRepository extends JpaRepository<Sermon, Long> {
    Optional<Sermon> findByActiveTrue();
    List<Sermon> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}