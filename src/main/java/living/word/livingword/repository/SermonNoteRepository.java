package living.word.livingword.repository;

import living.word.livingword.entity.SermonNote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SermonNoteRepository extends JpaRepository<SermonNote, Long> {
    List<SermonNote> findAllByOrderByDateAsc();
    List<SermonNote> findAllByOrderByDateDesc();
}
