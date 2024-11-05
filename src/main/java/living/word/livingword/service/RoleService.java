package living.word.livingword.service;

import living.word.livingword.entity.Permission;
import living.word.livingword.entity.Role;
import living.word.livingword.entity.User;
import living.word.livingword.exception.RoleNotFoundException;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AppUserRepository userRepository;

    // Crear un nuevo rol
    public Role createRole(String name, int level, Set<String> permissionNames) {
        if(roleRepository.findByName(name).isPresent()){
            throw new IllegalArgumentException("The role already exists");
        }
        Role role = new Role();
        role.setName(name);
        role.setLevel(level);
        Set<Permission> permissions = permissionNames.stream()
                .map(permissionService::getPermissionByName)
                .collect(Collectors.toSet());
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    // Obtener todos los roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Obtener rol por ID
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
    }
    // Obtener rol por nombre
    public Optional<Role> getRoleByName(String name){
        return roleRepository.findByName(name);
    }

    // Actualizar permisos de un rol
    @Transactional
    public Role updateRolePermissions(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));
        Set<Permission> permissions = permissionNames.stream()
                .map(permissionService::getPermissionByName)
                .collect(Collectors.toSet());
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    // Eliminar un rol
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Rol no encontrado"));

        // No permitir eliminar roles USER y ADMINISTRATOR
        if(role.getName().equalsIgnoreCase("USER") || role.getName().equalsIgnoreCase("ADMINISTRATOR")){
            throw new IllegalArgumentException("No se puede eliminar los roles USER o ADMINISTRATOR");
        }

        // Obtener el rol USER para reasignar a los usuarios
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));

        // Reasignar usuarios al rol USER
        List<User> usersWithRole = userRepository.findByRole(role);
        for(User user : usersWithRole){
            user.setRole(userRole);
            userRepository.save(user);
        }

        // Eliminar el rol
        roleRepository.delete(role);
    }
}