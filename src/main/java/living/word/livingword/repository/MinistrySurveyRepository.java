package living.word.livingword.repository;

import living.word.livingword.entity.MinistrySurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinistrySurveyRepository extends JpaRepository<MinistrySurvey, Long> {
    List<MinistrySurvey> findByMinistryId(Long ministryId);  // Listar encuestas por ministerio
    List<MinistrySurvey> findByMinistryIdAndResponse(Long ministryId, MinistrySurvey.ParticipationResponse response);  // Filtrar respuestas
    List<MinistrySurvey> findByUserId(Long userId);  // Encuestas por usuario
    MinistrySurvey findByMinistryIdAndUserId(Long ministryId, Long userId); 
    void deleteAllByMinistryId(Long ministryId);
}