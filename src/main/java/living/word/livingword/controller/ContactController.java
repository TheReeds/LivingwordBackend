package living.word.livingword.controller;

import living.word.livingword.entity.Contact;
import living.word.livingword.entity.User;
import living.word.livingword.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import living.word.livingword.model.dto.ContactDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PreAuthorize("hasAnyAuthority('PERM_CONTACT_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping
    public ResponseEntity<List<ContactDto>> getAllContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        List<ContactDto> contactDtos = contacts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contactDtos);
    }

    @PreAuthorize("hasAnyAuthority('PERM_CONTACT_READ', 'PERM_ADMIN_ACCESS')")
    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        return contact.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('PERM_CONTACT_WRITE', 'PERM_ADMIN_ACCESS')")
    @PostMapping
    public ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto) {
        Contact contact = convertToEntity(contactDto);
        
        // Obtener el usuario autenticado
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        contact.setAddedBy(authenticatedUser);

        Contact savedContact = contactService.addOrUpdateContact(contact);
        return ResponseEntity.ok(convertToDto(savedContact));
    }

    @PreAuthorize("hasAnyAuthority('PERM_CONTACT_EDIT', 'PERM_ADMIN_ACCESS')")
    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto) {
        Optional<Contact> existingContact = contactService.getContactById(id);
        if (existingContact.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Contact contact = existingContact.get();
        contact.setName(contactDto.getName());
        contact.setPhone(contactDto.getPhone());
        contact.setEmail(contactDto.getEmail());
        // No cambiamos el `addedBy` ya que es solo de creación
        
        Contact updatedContact = contactService.addOrUpdateContact(contact);
        return ResponseEntity.ok(convertToDto(updatedContact));
    }

    @PreAuthorize("hasAnyAuthority('PERM_CONTACT_DELETE', 'PERM_ADMIN_ACCESS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos de conversión
    private ContactDto convertToDto(Contact contact) {
        ContactDto dto = new ContactDto();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setPhone(contact.getPhone());
        dto.setEmail(contact.getEmail());
        if (contact.getAddedBy() != null) {
            dto.setAddedById(contact.getAddedBy().getId());
            dto.setAddedByName(contact.getAddedBy().getName());
            dto.setAddedByLastname(contact.getAddedBy().getLastname());
        }
        return dto;
    }

    private Contact convertToEntity(ContactDto dto) {
        Contact contact = new Contact();
        contact.setId(dto.getId());
        contact.setName(dto.getName());
        contact.setPhone(dto.getPhone());
        contact.setEmail(dto.getEmail());
        return contact;
    }
}
