package living.word.livingword.controller;

import living.word.livingword.entity.Ministry;
import living.word.livingword.model.dto.MinistryDto;
import living.word.livingword.model.dto.UserDTO;
import living.word.livingword.service.MinistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ministries")
public class MinistryController {

    @Autowired
    private MinistryService ministryService;

    // Crear un ministerio
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_WRITE')")
    public Ministry createMinistry(@RequestParam String name, String description) {
        return ministryService.createMinistry(name, description);
    }

    // Editar un ministerio
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_EDIT')")
    public void editMinistry(@PathVariable Long id, @RequestParam String newName, String newDescription) {
        ministryService.editMinistry(id, newName, newDescription);
    }

    // Borrar un ministerio
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_DELETE')")
    public void deleteMinistry(@PathVariable Long id) {
        ministryService.deleteMinistry(id);
    }

    // Afiliar un usuario a un ministerio
    @PostMapping("/affiliate")
    @PreAuthorize("(hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_ASSIGNATE')) and principal.role.level >= 1")
    public void affiliateUserToMinistry(@RequestParam Long ministryId, @RequestParam Long userId) {
        ministryService.affiliateUserToMinistry(ministryId, userId);
    }

    // Listar todos los ministerios
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_READ')")
    public List<MinistryDto> getAllMinistries() {
        return ministryService.getAllMinistries().stream()
                              .map(ministryService::toMinistryDTO)
                              .collect(Collectors.toList());
    }

    // Asignar un líder a un ministerio
    @PostMapping("/assign-leader")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_WRITE', 'PERM_MINISTRY_ASSIGNATE')")
    public void assignLeaderToMinistry(@RequestParam Long ministryId, @RequestParam Long userId) {
        ministryService.assignLeaderToMinistry(ministryId, userId);
    }

    // Obtener un ministerio por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_READ')")
    public MinistryDto getMinistryById(@PathVariable Long id) {
        Ministry ministry = ministryService.findById(id);
        return ministryService.toMinistryDTO(ministry);
    }
    // Quitar un líder de un ministerio
    @DeleteMapping("/remove-leader")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_WRITE', 'PERM_MINISTRY_ASSIGNATE')")
    public void removeLeaderFromMinistry(@RequestParam Long ministryId, @RequestParam Long userId) {
        ministryService.removeLeaderFromMinistry(ministryId, userId);
    }
    @GetMapping("/{id}/members")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_READ')")
    public List<UserDTO> getMembersOfMinistry(@PathVariable Long id) {
        return ministryService.getMembersOfMinistry(id);
    }

}