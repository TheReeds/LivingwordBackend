package living.word.livingword.controller;

import living.word.livingword.entity.SermonNote;
import living.word.livingword.entity.User;
import living.word.livingword.model.dto.SermonnoteDto;
import living.word.livingword.service.SermonNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/sermonnotes")
public class SermonNoteController {

    @Autowired
    private SermonNoteService sermonNoteService;

    @PreAuthorize("hasAnyAuthority('PERM_SERMONNOTE_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping
    public ResponseEntity<Page<SermonnoteDto>> getAllSermonNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort) {
        Page<SermonNote> sermonNotes = sermonNoteService.getAllSermonNotes(page, size, sort.equals("desc"));
        Page<SermonnoteDto> dtoPage = sermonNotes.map(this::convertToDto);
        return ResponseEntity.ok(dtoPage);
    }

    @PreAuthorize("hasAnyAuthority('PERM_SERMONNOTE_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping("/{id}")
    public ResponseEntity<SermonnoteDto> getSermonNoteById(@PathVariable Long id) {
        Optional<SermonNote> sermonNote = sermonNoteService.getSermonNoteById(id);
        return sermonNote.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('PERM_SERMONNOTE_WRITE', 'PERM_ADMIN_ACCESS')")
    @PostMapping
    public ResponseEntity<SermonnoteDto> createSermonNote(@RequestBody SermonnoteDto dto) {
        SermonNote sermonNote = convertToEntity(dto);

        // Establecer la fecha actual
        sermonNote.setDate(LocalDateTime.now());

        // Obtener el usuario autenticado
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sermonNote.setUploadedBy(authenticatedUser);

        SermonNote createdNote = sermonNoteService.createSermonNote(sermonNote);
        return new ResponseEntity<>(convertToDto(createdNote), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('PERM_SERMONNOTE_EDIT', 'PERM_ADMIN_ACCESS')")
    @PutMapping("/{id}")
    public ResponseEntity<SermonnoteDto> updateSermonNote(@PathVariable Long id, @RequestBody SermonnoteDto dto) {
        SermonNote sermonNote = convertToEntity(dto);
        Optional<SermonNote> updatedNote = sermonNoteService.updateSermonNote(id, sermonNote);
        return updatedNote.map(note -> new ResponseEntity<>(convertToDto(note), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('PERM_SERMONNOTE_DELETE', 'PERM_ADMIN_ACCESS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSermonNote(@PathVariable Long id) {
        sermonNoteService.deleteSermonNote(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos de conversión
    private SermonnoteDto convertToDto(SermonNote sermonNote) {
        SermonnoteDto dto = new SermonnoteDto();
        dto.setId(sermonNote.getId());
        dto.setTitle(sermonNote.getTitle());
        dto.setSermonurl(sermonNote.getSermonurl());
        dto.setDate(sermonNote.getDate());
        if (sermonNote.getUploadedBy() != null) {
            dto.setAddedById(sermonNote.getUploadedBy().getId());
            dto.setAddedByName(sermonNote.getUploadedBy().getName());
            dto.setAddedByLastname(sermonNote.getUploadedBy().getLastname());
        }
        return dto;
    }

    private SermonNote convertToEntity(SermonnoteDto dto) {
        SermonNote sermonNote = new SermonNote();
        sermonNote.setId(dto.getId());
        sermonNote.setTitle(dto.getTitle());
        sermonNote.setSermonurl(dto.getSermonurl());
        // La fecha se asigna en createSermonNote, por lo que aquí no es necesario
        return sermonNote;
    }
}
