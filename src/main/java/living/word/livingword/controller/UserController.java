package living.word.livingword.controller;

import living.word.livingword.entity.User;
import living.word.livingword.model.dto.AssignRoleRequest;
import living.word.livingword.model.dto.UpdateUserDto;
import living.word.livingword.model.dto.UserDTO;
import living.word.livingword.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Asignar rol a un usuario
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_WRITE')")
    @PostMapping("/assign-role")
    public ResponseEntity<User> assignRoleToUser(@Valid @RequestBody AssignRoleRequest assignRoleRequest) {
        try {
            User user = userService.assignRoleToUser(assignRoleRequest.getUserId(), assignRoleRequest.getRoleId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Remover rol de un usuario (volver a USER)
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_WRITE')")
    @PostMapping("/remove-role")
    public ResponseEntity<User> removeRoleFromUser(@Valid @RequestBody AssignRoleRequest assignRoleRequest) {
        try {
            User user = userService.removeRoleFromUser(assignRoleRequest.getUserId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_READ','MINISTRY_ASSIGNATE')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    // Buscar un usuario por ID
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_READ','MINISTRY_ASSIGNATE')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    // Editar un usuario
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_EDIT')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, 
                                            @Valid @RequestBody UpdateUserDto updateUserDto) {
        try {
            UserDTO updatedUser = userService.updateUser(userId, updateUserDto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    // Eliminar un usuario
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_DELETE')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/updateProfileImage")
    public ResponseEntity<String> updateProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            userService.updateUserProfilePhoto(file);
            return ResponseEntity.ok("Foto de perfil actualizada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la foto de perfil.");
        }
    }
    @GetMapping("/profileImage/{filename}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String filename) {
        try {
            byte[] imageBytes = userService.getProfileImage(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // O ajusta según el tipo de imagen
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @DeleteMapping("/deleteProfileImage")
    public ResponseEntity<String> deleteProfileImage() {
        try {
            userService.deleteUserProfilePhoto();
            return ResponseEntity.ok("Foto de perfil eliminada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la foto de perfil.");
        }
    }
}
