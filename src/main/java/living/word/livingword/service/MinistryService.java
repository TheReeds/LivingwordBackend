package living.word.livingword.service;

import jakarta.transaction.Transactional;
import living.word.livingword.entity.Ministry;
import living.word.livingword.entity.User;
import living.word.livingword.exception.AccessDeniedException;
import living.word.livingword.exception.ResourceNotFoundException;
import living.word.livingword.model.dto.MinistryDto;
import living.word.livingword.model.dto.UserDTO;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.MinistryRepository;
import living.word.livingword.repository.MinistrySurveyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MinistryService {

    @Autowired
    private MinistryRepository ministryRepository;

    @Autowired
    private MinistrySurveyRepository ministrySurveyRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No se encontró un usuario autenticado");
    }

    // Crear un ministerio (solo usuarios con nivel ADMINISTRATOR o DEPARTMENT_LEADER)
    @Transactional
    public Ministry createMinistry(String name, String description) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().getLevel() < 2) {
            throw new AccessDeniedException("No tienes permisos para crear un ministerio.");
        }

        Ministry ministry = new Ministry();
        ministry.setName(name);
        ministry.setDescription(description);
        return ministryRepository.save(ministry);
    }

    // Editar un ministerio
    @Transactional
    public void editMinistry(Long ministryId, String newName, String newDescription) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().getLevel() < 2) {
            throw new AccessDeniedException("No tienes permisos para editar un ministerio.");
        }

        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));
        ministry.setName(newName);
        ministry.setDescription(newDescription);
    }

    @Transactional
    public void deleteMinistry(Long ministryId) {
        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));
    
        // Eliminar todas las respuestas (encuestas) relacionadas con este ministerio
        ministrySurveyRepository.deleteAllByMinistryId(ministryId);
    
        // Desvincular a todos los usuarios de este ministerio
        List<User> users = appUserRepository.findByMinistry(ministry);
        for (User user : users) {
            user.setMinistry(null);
            appUserRepository.save(user);
        }
    
        // Eliminar todos los líderes de este ministerio
        ministry.getLeaders().clear();  // Esto limpia la lista de líderes en el ministerio
        ministryRepository.save(ministry);
    
        // Eliminar el ministerio
        ministryRepository.delete(ministry);
    }


    // Afiliar un usuario a un ministerio
    @Transactional
    public void affiliateUserToMinistry(Long ministryId, Long userId) {
        User user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (ministryId == 0) {
            // Si ministryId es 0, asignamos null al ministerio del usuario
            user.setMinistry(null);
        } else {
            Ministry ministry = ministryRepository.findById(ministryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));

            // Si el usuario es líder de otro ministerio, lo quitamos de ese ministerio
            if (user.getMinistry() != null && !user.getMinistry().getId().equals(ministryId)) {
                Ministry oldMinistry = user.getMinistry();
                oldMinistry.getLeaders().remove(user); // Lo quitamos de los líderes del ministerio anterior
                ministryRepository.save(oldMinistry);
            }

            user.setMinistry(ministry); // Asociamos al usuario con el nuevo ministerio
        }

        appUserRepository.save(user);
    }


    // Asignar un líder a un ministerio
    @Transactional
    public void assignLeaderToMinistry(Long ministryId, Long userId) {
        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));
        
        User user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar si el usuario tiene el rol correcto
        if (!user.getRole().getName().equals("LEADER")) {
            throw new AccessDeniedException("El usuario no tiene el rol de líder.");
        }

        // Verificar si el usuario ya es líder de este ministerio
        if (ministry.getLeaders().contains(user)) {
            throw new IllegalArgumentException("This user is already leader of this ministry.");
        }

        // Asignar al usuario como líder del ministerio
        ministry.getLeaders().add(user);  // Asegúrate de que existe un método getLeaders en Ministry
        ministryRepository.save(ministry);
    }
    // Quitar un líder de un ministerio
    @Transactional
    public void removeLeaderFromMinistry(Long ministryId, Long userId) {
        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministry not found"));

        User user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verificar si el usuario es líder de este ministerio
        if (!ministry.getLeaders().contains(user)) {
            throw new IllegalArgumentException("El usuario no es líder de este ministerio.");
        }

        // Remover al usuario de la lista de líderes del ministerio
        ministry.getLeaders().remove(user);
        ministryRepository.save(ministry);
    }


    // Listar todos los ministerios
    public List<Ministry> getAllMinistries() {
        return ministryRepository.findAll();
    }
    public Ministry findById(Long id) {
        return ministryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));
    }

    public MinistryDto toMinistryDTO(Ministry ministry) {
        MinistryDto dto = new MinistryDto();
        dto.setId(ministry.getId());
        dto.setName(ministry.getName());
        dto.setDescription(ministry.getDescription());
        // Mapear líderes directamente si la entidad Ministry contiene una lista de líderes
        dto.setLeaders(ministry.getLeaders().stream()
            .map(this::toUserDTO)
            .collect(Collectors.toList()));
        return dto;
    }
    // Obtener todos los miembros de un ministerio
    public List<UserDTO> getMembersOfMinistry(Long ministryId) {
        Ministry ministry = ministryRepository.findById(ministryId)
                .orElseThrow(() -> new ResourceNotFoundException("Ministry not found"));
        return ministry.getMembers().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        return dto;
    }

}