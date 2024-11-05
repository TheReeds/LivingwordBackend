package living.word.livingword.service;

import living.word.livingword.model.dto.EventCreateRequest;
import living.word.livingword.model.dto.EventDto;
import living.word.livingword.entity.Event;
import living.word.livingword.entity.Ministry;
import living.word.livingword.entity.User;
import living.word.livingword.exception.AccessDeniedException;
import living.word.livingword.exception.EventNotFoundException;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Obtener el usuario autenticado
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No se encontró un usuario autenticado");
    }

    // Crear un evento solo para los usuarios del ministerio del líder
    @Transactional
    public EventDto createEvent(EventCreateRequest eventRequest) {
        User currentUser = getCurrentUser();

        Ministry ministry = currentUser.getMinistry();  // Obtener el ministerio del líder

        Event event = new Event();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setCreatedBy(currentUser);
        event.setMinistry(ministry);

        Event savedEvent = eventRepository.save(event);

        // Notificar a los usuarios del ministerio
        List<User> ministryUsers = userRepository.findByMinistry(ministry);
        //notificationService.sendEventNotification(savedEvent, ministryUsers);

        return convertToDto(savedEvent);
    }

    // Editar evento (niveles 3 y 4)
    @Transactional
    public EventDto editEvent(Long eventId, EventDto eventDto) {
        User currentUser = getCurrentUser();

        // Validar si el usuario tiene nivel 3 o 4
        if (currentUser.getRole().getLevel() < 3) { // Suponiendo que Role enum tiene un método getLevel()
            throw new AccessDeniedException("No tienes permiso para editar eventos.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado"));

        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());

        Event updatedEvent = eventRepository.save(event);
        return convertToDto(updatedEvent);
    }

    // Convertir entidad a DTO
    private EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setCreatedByUsername(event.getCreatedBy().getName()); // O getUsername() según tu implementación
        return dto;
    }
}

/*@Service
public class NewsletterService {

    private final Path rootLocation; // Folder to store images

    @Autowired
    private NotificationService notificationService;

    @Autowired
    public NewsletterService(@Value("${newsletter.images.path}") String imagesPath) {
        this.rootLocation = Paths.get(imagesPath);
        init();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Autowired
    private NewsletterRepository newsletterRepository;


    // Get User Authenticate
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    // Save an image file and return its unique filename
    // Sanitize filename to prevent path traversal attacks
    private String sanitizeFilename(String filename) {
        return Paths.get(filename).getFileName().toString();
    }

    // Save an image file and return its unique filename
    private String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Image file is empty");
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new FileStorageException("File must be an image (JPEG or PNG)");
        }

        String originalFilename = sanitizeFilename(file.getOriginalFilename());
        String fileExtension = "";

        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path destinationFile = rootLocation.resolve(Paths.get(uniqueFilename)).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
            throw new FileStorageException("Cannot store file outside current directory.");
        }

        try {
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + uniqueFilename, e);
        }

        return uniqueFilename;
    }

    // Create and publish a newsletter with image
    public Newsletter createNewsletter(String title, String content, MultipartFile imageFile) {
        String imageUrl = saveImage(imageFile);
        User currentUser = getCurrentUser();
    
        Newsletter newsletter = new Newsletter();
        newsletter.setTitle(title);
        newsletter.setContent(content);
        newsletter.setImageUrl(imageUrl); // Establece la URL de la imagen
        newsletter.setPublicationDate(LocalDateTime.now());
        newsletter.setUploadedBy(currentUser); // Usuario que sube el newsletter
    
        Newsletter savedNewsletter = newsletterRepository.save(newsletter);
    
        // Enviar notificación después de guardar el newsletter
        notificationService.sendNewsletterNotification(savedNewsletter);
    
        return savedNewsletter;
    }

    // Get all newsletters
    public List<Newsletter> getAllNewsletters() {
        return newsletterRepository.findAll();
    }

    // Get a specific newsletter by id
    public Optional<Newsletter> getNewsletterById(Long id) {
        return newsletterRepository.findById(id);
    }

    // Delete a newsletter by id and remove its image
    public void deleteNewsletter(Long id) {
        Newsletter newsletter = newsletterRepository.findById(id)
                .orElseThrow(() -> new NewsletterNotFoundException("Newsletter with id " + id + " not found."));

        String imageFilename = newsletter.getImageUrl();

        // Eliminar el newsletter de la base de datos
        newsletterRepository.deleteById(id);

        // Eliminar la imagen del sistema de archivos
        try {
            Path imagePath = rootLocation.resolve(imageFilename).normalize();
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new FileStorageException("Error deleting image: " + imageFilename, e);
        }
    }

    // Fetch the image by filename
    public byte[] getNewsletterImage(String filename) {
        try {
            Path filePath = rootLocation.resolve(filename).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Could not read the image: " + filename, e);
        }
    }
    // Convert to Dto
    public NewsletterDto convertToDto(Newsletter newsletter) {
        NewsletterDto dto = new NewsletterDto();
        dto.setId(newsletter.getId());
        dto.setTitle(newsletter.getTitle());
        dto.setContent(newsletter.getContent());
        dto.setImageUrl(newsletter.getImageUrl());
        dto.setPublicationDate(newsletter.getPublicationDate());

        // Null in uploadedBy
        if (newsletter.getUploadedBy() != null) {
            dto.setUploadedByUsername(newsletter.getUploadedBy().getUsername());
        } else {
            dto.setUploadedByUsername("Unknown");
        }

        return dto;
    }
} */