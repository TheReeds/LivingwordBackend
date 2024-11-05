package living.word.livingword.service;

import living.word.livingword.entity.Permission;
import living.word.livingword.exception.PermissionNotFoundException;
import living.word.livingword.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    // Crear un nuevo permiso
    public Permission createPermission(String name) {
        if(permissionRepository.findByName(name).isPresent()){
            throw new IllegalArgumentException("El permiso ya existe");
        }
        Permission permission = new Permission(name);
        return permissionRepository.save(permission);
    }

    // Obtener todos los permisos
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    // Obtener permiso por nombre
    public Permission getPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado: " + name));
    }

    // Obtener permiso por ID
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));
    }

    // Actualizar permiso
    public Permission updatePermission(Long id, String name) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));

        permission.setName(name);
        return permissionRepository.save(permission);
    }

    // Eliminar permiso
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permiso no encontrado"));
        permissionRepository.delete(permission);
    }
}