package living.word.livingword.repository;

import living.word.livingword.entity.GiftsAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GiftsAssessmentRepository extends JpaRepository<GiftsAssessment, Long> {
    Optional<GiftsAssessment> findByUserId(Long userId);
}
