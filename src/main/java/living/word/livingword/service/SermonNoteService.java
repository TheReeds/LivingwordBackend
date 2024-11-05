package living.word.livingword.service;

import living.word.livingword.entity.SermonNote;
import living.word.livingword.repository.SermonNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class SermonNoteService {

    @Autowired
    private SermonNoteRepository sermonNoteRepository;

    public Page<SermonNote> getAllSermonNotes(int page, int size, boolean descending) {
        Sort sort = descending ? Sort.by("date").descending() : Sort.by("date").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return sermonNoteRepository.findAll(pageable);
    }

    public Optional<SermonNote> getSermonNoteById(Long id) {
        return sermonNoteRepository.findById(id);
    }

    public SermonNote createSermonNote(SermonNote sermonNote) {
        return sermonNoteRepository.save(sermonNote);
    }

    public Optional<SermonNote> updateSermonNote(Long id, SermonNote sermonNote) {
        return sermonNoteRepository.findById(id).map(existingNote -> {
            existingNote.setTitle(sermonNote.getTitle());
            existingNote.setSermonurl(sermonNote.getSermonurl());
            return sermonNoteRepository.save(existingNote);
        });
    }

    public void deleteSermonNote(Long id) {
        sermonNoteRepository.deleteById(id);
    }
}
