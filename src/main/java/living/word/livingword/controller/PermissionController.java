package living.word.livingword.controller;
import jakarta.validation.Valid;
import living.word.livingword.entity.Permission;
import living.word.livingword.model.dto.PermissionDto;
import living.word.livingword.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // Crear un nuevo permiso
    @PostMapping("/create")
    public ResponseEntity<PermissionDto> createPermission(@Valid @RequestBody PermissionDto permissionDto) {
        Permission permission = permissionService.createPermission(permissionDto.getName());
        PermissionDto responseDto = convertToDto(permission);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Obtener todos los permisos
    @GetMapping
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(permissionDtos, HttpStatus.OK);
    }

    // Obtener permiso por ID
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        PermissionDto permissionDto = convertToDto(permission);
        return new ResponseEntity<>(permissionDto, HttpStatus.OK);
    }

    // Actualizar permiso
    @PutMapping("/{id}")
    public ResponseEntity<PermissionDto> updatePermission(@PathVariable Long id, @RequestBody PermissionDto permissionDto) {
        Permission updatedPermission = permissionService.updatePermission(id, permissionDto.getName());
        PermissionDto responseDto = convertToDto(updatedPermission);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // Eliminar permiso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return new ResponseEntity<>("Permiso eliminado exitosamente", HttpStatus.NO_CONTENT);
    }

    // Convertir entidad a DTO
    private PermissionDto convertToDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        return dto;
    }
}