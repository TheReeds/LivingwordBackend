package living.word.livingword.controller;

import living.word.livingword.entity.Sermon;
import living.word.livingword.model.dto.SermonDto;
import living.word.livingword.model.dto.SermonAttendanceStats;
import living.word.livingword.service.SermonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sermons")
public class SermonController {

    @Autowired
    private SermonService sermonService;

    // Crear un nuevo sermon
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_WRITE', 'PERM_ADMIN_ACCESS')")
    @PostMapping
    public ResponseEntity<SermonDto> createSermon(@Valid @RequestBody SermonDto sermonDto) {
        Sermon sermon = convertToEntity(sermonDto);
        Sermon createdSermon = sermonService.createSermon(sermon);
        SermonDto responseDto = convertToDto(createdSermon);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Actualizar un sermon
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_EDIT', 'PERM_ADMIN_ACCESS')")
    @PutMapping("/{id}")
    public ResponseEntity<SermonDto> updateSermon(@PathVariable Long id, @Valid @RequestBody SermonDto sermonDto) {
        Sermon sermon = convertToEntity(sermonDto);
        Sermon updatedSermon = sermonService.updateSermon(id, sermon);
        SermonDto responseDto = convertToDto(updatedSermon);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // Obtener todos los sermones
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping
    public ResponseEntity<List<SermonDto>> getAllSermons() {
        List<Sermon> sermons = sermonService.getAllSermons();
        List<SermonDto> sermonDtos = sermons.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(sermonDtos, HttpStatus.OK);
    }

    // Obtener sermo por ID
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping("/{id}")
    public ResponseEntity<SermonDto> getSermonById(@PathVariable Long id) {
        Sermon sermon = sermonService.getSermonById(id);
        SermonDto sermonDto = convertToDto(sermon);
        return new ResponseEntity<>(sermonDto, HttpStatus.OK);
    }

    // Iniciar sermon
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_EDIT', 'PERM_ADMIN_ACCESS')")
    @PostMapping("/{id}/start")
    public ResponseEntity<SermonDto> startSermon(@PathVariable Long id) {
        Sermon sermon = sermonService.startSermon(id);
        SermonDto sermonDto = convertToDto(sermon);
        return new ResponseEntity<>(sermonDto, HttpStatus.OK);
    }

    // Finalizar sermon
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_EDIT', 'PERM_ADMIN_ACCESS')")
    @PostMapping("/{id}/end")
    public ResponseEntity<SermonDto> endSermon(@PathVariable Long id) {
        Sermon sermon = sermonService.endSermon(id);
        SermonDto sermonDto = convertToDto(sermon);
        return new ResponseEntity<>(sermonDto, HttpStatus.OK);
    }

    // Obtener estadísticas de asistencia de un sermo
    @PreAuthorize("hasAnyAuthority('PERM_SERMON_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping("/{id}/attendance-stats")
    public ResponseEntity<SermonAttendanceStats> getSermonAttendanceStats(@PathVariable Long id) {
        SermonAttendanceStats stats = sermonService.getSermonAttendanceStats(id);
        return ResponseEntity.ok(stats);
    }

    // Convertir DTO a entidad
    private Sermon convertToEntity(SermonDto dto) {
        Sermon sermon = new Sermon();
        sermon.setTitle(dto.getTitle());
        sermon.setDescription(dto.getDescription());
        sermon.setStartTime(dto.getStartTime());
        sermon.setEndTime(dto.getEndTime());
        sermon.setVideoLink(dto.getVideoLink());
        sermon.setSummary(dto.getSummary());
        return sermon;
    }

    // Convertir entidad a DTO
    private SermonDto convertToDto(Sermon sermon) {
        SermonDto dto = new SermonDto();
        dto.setId(sermon.getId());
        dto.setTitle(sermon.getTitle());
        dto.setDescription(sermon.getDescription());
        dto.setStartTime(sermon.getStartTime());
        dto.setEndTime(sermon.getEndTime());
        dto.setVideoLink(sermon.getVideoLink());
        dto.setSummary(sermon.getSummary());
        dto.setActive(sermon.isActive());
        return dto;
    }
}