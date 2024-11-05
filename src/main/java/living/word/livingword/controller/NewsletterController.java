package living.word.livingword.controller;

import living.word.livingword.entity.Newsletter;
import living.word.livingword.entity.User;
import living.word.livingword.model.dto.NewsletterDto;
import living.word.livingword.service.NewsletterService;
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
@RequestMapping("/newsletters")
public class NewsletterController {

    @Autowired
    private NewsletterService newsletterService;

    @PreAuthorize("hasAnyAuthority('PERM_NEWSLETTER_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping
    public ResponseEntity<Page<NewsletterDto>> getAllNewsletters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort) {
        Page<Newsletter> newsletters = newsletterService.getAllNewsletters(page, size, sort.equals("desc"));
        Page<NewsletterDto> dtoPage = newsletters.map(this::convertToDto);
        return ResponseEntity.ok(dtoPage);
    }

    @PreAuthorize("hasAnyAuthority('PERM_NEWSLETTER_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping("/{id}")
    public ResponseEntity<NewsletterDto> getNewsletterById(@PathVariable Long id) {
        Optional<Newsletter> newsletter = newsletterService.getNewsletterById(id);
        return newsletter.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('PERM_NEWSLETTER_WRITE', 'PERM_ADMIN_ACCESS')")
    @PostMapping
    public ResponseEntity<NewsletterDto> createNewsletter(@RequestBody NewsletterDto dto) {
        Newsletter newsletter = convertToEntity(dto);

        // Establecer la fecha de publicación
        newsletter.setPublicationDate(LocalDateTime.now());

        // Obtener el usuario autenticado
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        newsletter.setUploadedBy(authenticatedUser);

        Newsletter createdNewsletter = newsletterService.createNewsletter(newsletter);
        return new ResponseEntity<>(convertToDto(createdNewsletter), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('PERM_NEWSLETTER_EDIT', 'PERM_ADMIN_ACCESS')")
    @PutMapping("/{id}")
    public ResponseEntity<NewsletterDto> updateNewsletter(@PathVariable Long id, @RequestBody NewsletterDto dto) {
        Newsletter newsletter = convertToEntity(dto);
        Optional<Newsletter> updatedNewsletter = newsletterService.updateNewsletter(id, newsletter);
        return updatedNewsletter.map(n -> new ResponseEntity<>(convertToDto(n), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('PERM_NEWSLETTER_DELETE', 'PERM_ADMIN_ACCESS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNewsletter(@PathVariable Long id) {
        newsletterService.deleteNewsletter(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos de conversión
    private NewsletterDto convertToDto(Newsletter newsletter) {
        NewsletterDto dto = new NewsletterDto();
        dto.setId(newsletter.getId());
        dto.setTitle(newsletter.getTitle());
        dto.setNewsletterUrl(newsletter.getNewsletterUrl());
        dto.setPublicationDate(newsletter.getPublicationDate());
        if (newsletter.getUploadedBy() != null) {
            dto.setUploadedById(newsletter.getUploadedBy().getId());
            dto.setUploadedByFirstName(newsletter.getUploadedBy().getName());
            dto.setUploadedBySecondName(newsletter.getUploadedBy().getLastname());
        }
        return dto;
    }

    private Newsletter convertToEntity(NewsletterDto dto) {
        Newsletter newsletter = new Newsletter();
        newsletter.setId(dto.getId());
        newsletter.setTitle(dto.getTitle());
        newsletter.setNewsletterUrl(dto.getNewsletterUrl());
        return newsletter;
    }
}