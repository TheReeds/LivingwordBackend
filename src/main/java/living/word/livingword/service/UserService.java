package living.word.livingword.service;

import jakarta.transaction.Transactional;
import living.word.livingword.entity.Role;
import living.word.livingword.entity.User;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    // Asignar rol a un usuario
    @Transactional
    public User assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        // Agregar permisos básicos de USER si el nuevo rol no es USER
        if(!role.getName().equalsIgnoreCase("USER")){
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));
            role.getPermissions().addAll(userRole.getPermissions());
        }

        user.setRole(role);
        return userRepository.save(user);
    }

    // Remover rol de un usuario (volver a USER)
    @Transactional
    public User removeRoleFromUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));

        user.setRole(userRole);
        return userRepository.save(user);
    }
}