package living.word.livingword.controller;

import living.word.livingword.entity.Ministry;
import living.word.livingword.service.MinistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ministries")
public class MinistryController {

    @Autowired
    private MinistryService ministryService;

    // Crear un ministerio
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_WRITE')")
    public Ministry createMinistry(@RequestParam String name) {
        return ministryService.createMinistry(name);
    }

    // Editar un ministerio
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_EDIT')")
    public Ministry editMinistry(@PathVariable Long id, @RequestParam String newName) {
        return ministryService.editMinistry(id, newName);
    }

    // Borrar un ministerio
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_DELETE')")
    public void deleteMinistry(@PathVariable Long id) {
        ministryService.deleteMinistry(id);
    }

    // Afiliar un usuario a un ministerio
    @PostMapping("/affiliate")
    @PreAuthorize("(hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_WRITE')) and principal.role.level >= 2")
    public void affiliateUserToMinistry(@RequestParam Long ministryId, @RequestParam Long userId) {
        ministryService.affiliateUserToMinistry(ministryId, userId);
    }

    // Listar todos los ministerios
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('PERM_ADMIN_ACCESS','PERM_MINISTRY_READ')")
    public List<Ministry> getAllMinistries() {
        return ministryService.getAllMinistries();
    }
}