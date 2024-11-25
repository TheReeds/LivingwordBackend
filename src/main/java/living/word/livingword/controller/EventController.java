package living.word.livingword.controller;

import living.word.livingword.exception.FileStorageException;
import living.word.livingword.model.dto.EventCreateRequest;
import living.word.livingword.model.dto.EventDto;
import living.word.livingword.service.EventService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import living.word.livingword.exception.EventNotFoundException;
import living.word.livingword.exception.FileStorageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // Crear un nuevo evento
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_EVENT_WRITE')")
    public ResponseEntity<?> createEvent(
            @Valid @ModelAttribute EventCreateRequest eventRequest,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            EventDto createdEvent = eventService.createEvent(eventRequest, imageFile);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_EVENT_EDIT')")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute EventCreateRequest eventRequest,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            EventDto updatedEvent = eventService.updateEvent(id, eventRequest, imageFile);
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener todos los eventos con paginación y ordenación
    @GetMapping
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_EVENT_READ')")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<EventDto> events = eventService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Obtener un evento específico por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_EVENT_READ')")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            EventDto event = eventService.getEventById(id);
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Descargar imagen de evento
    @GetMapping("/images/{filename}")
    public ResponseEntity<?> getEventImage(@PathVariable String filename) {
        try {
            byte[] image = eventService.getEventImage(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(filename)));
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error determining content type", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar un evento específico por ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_EVENT_DELETE')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/ministry")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_EVENT_READ')")
    public ResponseEntity<List<EventDto>> getEventsByMinistry() {
        List<EventDto> events = eventService.getEventsByMinistry();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}

/*
 * @RestController
@RequestMapping("/newsletters")
public class NewsletterController {

    @Autowired
    private NewsletterService newsletterService;
    @Autowired
    private NewsletterRepository newsletterRepository;

    // Create a new newsletter
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_NEWSLETTER_WRITE')")
    public ResponseEntity<?> createNewsletter(
            @Valid @ModelAttribute NewsletterCreateRequest request,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            Newsletter newsletter = newsletterService.createNewsletter(request.getTitle(), request.getContent(), imageFile);
            return new ResponseEntity<>(newsletterService.convertToDto(newsletter), HttpStatus.CREATED);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all newsletters
    @GetMapping
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_NEWSLETTER_READ')")
    public ResponseEntity<List<NewsletterDto>> getAllNewsletters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection) { // New param for sort direction

        // Determine sort direction
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Create PageRequest with sorting by publicationDate
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "publicationDate"));

        List<Newsletter> newsletters = newsletterRepository.findAll(pageRequest).getContent();
        List<NewsletterDto> newsletterDtos = newsletters.stream()
                .map(newsletterService::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(newsletterDtos, HttpStatus.OK);
    }


    // Get a specific newsletter by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_NEWSLETTER_READ')")
    public ResponseEntity<?> getNewsletterById(@PathVariable Long id) {
        try {
            Newsletter newsletter = newsletterService.getNewsletterById(id)
                    .orElseThrow(() -> new NewsletterNotFoundException("Newsletter with id " + id + " not found."));
            return new ResponseEntity<>(newsletterService.convertToDto(newsletter), HttpStatus.OK);
        } catch (NewsletterNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get newsletter image
    @GetMapping("/images/{filename}")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_NEWSLETTER_READ')")
    public ResponseEntity<?> getNewsletterImage(@PathVariable String filename) {
        try {
            byte[] image = newsletterService.getNewsletterImage(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(filename)));
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error determining content type", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a newsletter
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ADMIN_ACCESS') or hasAuthority('PERM_NEWSLETTER_DELETE')")
    public ResponseEntity<?> deleteNewsletter(@PathVariable Long id) {
        try {
            newsletterService.deleteNewsletter(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NewsletterNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (FileStorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
 */