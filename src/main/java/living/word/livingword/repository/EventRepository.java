package living.word.livingword.repository;

import living.word.livingword.entity.Event;
import living.word.livingword.entity.Ministry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByMinistry(Ministry ministry);
}
