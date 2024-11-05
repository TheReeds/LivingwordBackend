package living.word.livingword.controller;

import living.word.livingword.entity.Role;
import living.word.livingword.model.dto.RoleDto;
import living.word.livingword.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Crear un nuevo rol
    @PostMapping("/create")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        Role role = roleService.createRole(roleDto.getName(), roleDto.getLevel(), roleDto.getPermissions());
        RoleDto responseDto = convertToDto(role);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Obtener todos los roles
    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDto> roleDtos = roles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(roleDtos, HttpStatus.OK);
    }

    // Obtener rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        RoleDto roleDto = convertToDto(role);
        return new ResponseEntity<>(roleDto, HttpStatus.OK);
    }

    // Actualizar permisos de un rol
    @PutMapping("/{id}/permissions")
    public ResponseEntity<RoleDto> updateRolePermissions(@PathVariable Long id, @RequestBody Set<String> permissions) {
        Role updatedRole = roleService.updateRolePermissions(id, permissions);
        RoleDto roleDto = convertToDto(updatedRole);
        return new ResponseEntity<>(roleDto, HttpStatus.OK);
    }

    // Eliminar un rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>("Rol eliminado exitosamente", HttpStatus.NO_CONTENT);
    }

    // Convertir entidad a DTO
    private RoleDto convertToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setLevel(role.getLevel());
        dto.setPermissions(role.getPermissions().stream()
                .map(permission -> permission.getName())
                .collect(Collectors.toSet()));
        return dto;
    }
}