package living.word.livingword.service;

import jakarta.transaction.Transactional;
import living.word.livingword.entity.Ministry;
import living.word.livingword.entity.User;
import living.word.livingword.exception.AccessDeniedException;
import living.word.livingword.exception.ResourceNotFoundException;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.MinistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinistryService {

    @Autowired
    private MinistryRepository ministryRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EventService eventService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No se encontró un usuario autenticado");
    }

    // Crear un ministerio (solo usuarios con nivel ADMINISTRATOR o DEPARTMENT_LEADER)
    @Transactional
    public Ministry createMinistry(String name) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().getLevel() < 2) {
            throw new AccessDeniedException("No tienes permisos para crear un ministerio.");
        }

        Ministry ministry = new Ministry();
        ministry.setName(name);
        return ministryRepository.save(ministry);
    }

    // Editar un ministerio
    @Transactional
    public Ministry editMinistry(Long ministryId, String newName) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole().getLevel() < 2) {
            throw new AccessDeniedException("No tienes permisos para editar un ministerio.");
        }

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new RuntimeException("Ministerio no encontrado"));

        ministry.setName(newName);
        return ministryRepository.save(ministry);
    }

    // Borrar un ministerio
    @Transactional
    public void deleteMinistry(Long ministryId) {
        // Busca el ministerio
        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));

        // Desasociar usuarios del ministerio
        List<User> users = appUserRepository.findByMinistry(ministry);
        for (User user : users) {
            user.setMinistry(null);
            appUserRepository.save(user);
        }

        // Finalmente, eliminar el ministerio
        ministryRepository.delete(ministry);
    }

    // Afiliar un usuario a un ministerio
    @Transactional
    public void affiliateUserToMinistry(Long ministryId, Long userId) {
        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new RuntimeException("Ministerio no encontrado"));

        User user = appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setMinistry(ministry);
        appUserRepository.save(user);
    }

    // Listar todos los ministerios
    public List<Ministry> getAllMinistries() {
        return ministryRepository.findAll();
    }
}