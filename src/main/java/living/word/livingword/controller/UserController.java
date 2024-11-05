package living.word.livingword.controller;

import living.word.livingword.entity.User;
import living.word.livingword.model.dto.AssignRoleRequest;
import living.word.livingword.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_USER_ACCESS')")
public class UserController {

    @Autowired
    private UserService userService;

    // Asignar rol a un usuario
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
    @PostMapping("/remove-role")
    public ResponseEntity<User> removeRoleFromUser(@Valid @RequestBody AssignRoleRequest assignRoleRequest) {
        try {
            User user = userService.removeRoleFromUser(assignRoleRequest.getUserId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
