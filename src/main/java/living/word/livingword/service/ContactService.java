package living.word.livingword.service;
import living.word.livingword.entity.Contact;
import living.word.livingword.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    // Add or update a contact (only for specific roles)
    public Contact addOrUpdateContact(Contact contact) {
        return contactRepository.save(contact);
    }

    // Obtener todos los contactos (disponible para todos los roles con permisos de lectura)
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    // Obtener un contacto por ID (disponible para roles con permisos de lectura)
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    // Eliminar un contacto (disponible solo para roles con permisos de eliminación)
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}
