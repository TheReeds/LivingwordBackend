package living.word.livingword.security;

import living.word.livingword.entity.Permission;
import living.word.livingword.entity.Role;
import living.word.livingword.entity.User;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.service.PermissionService;
import living.word.livingword.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin permissions
        Permission adminPermission = createPermissionIfNotExists("ADMIN_ACCESS");
        /* General Permissions
        - Permissions NewsLetters*/
        Permission readNewsletter = createPermissionIfNotExists("NEWSLETTER_READ");
        Permission writeNewsletter = createPermissionIfNotExists("NEWSLETTER_WRITE");
        Permission editNewsletter = createPermissionIfNotExists("NEWSLETTER_EDIT");
        Permission deleteNewsletter = createPermissionIfNotExists("NEWSLETTER_DELETE");
        // Video Permissions
        Permission readVideos = createPermissionIfNotExists("VIDEO_READ");
        Permission writeVideos = createPermissionIfNotExists("VIDEO_WRITE");
        Permission editVideos = createPermissionIfNotExists("VIDEO_EDIT");
        Permission deleteVideos = createPermissionIfNotExists("VIDEO_DELETE");
        //Attendance Permissions
        Permission readAttendance = createPermissionIfNotExists("ATTENDANCE_READ");
        Permission writeAttendance = createPermissionIfNotExists("ATTENDANCE_WRITE");
        Permission editAttendance = createPermissionIfNotExists("ATTENDANCE_EDIT");
        Permission deleteAttendance = createPermissionIfNotExists("ATTENDANCE_DELETE");
        // Prayer permissions
        Permission readPrayer = createPermissionIfNotExists("PRAYER_READ");
        Permission writePrayer = createPermissionIfNotExists("PRAYER_WRITE");
        Permission editPrayer = createPermissionIfNotExists("PRAYER_EDIT");
        Permission deletePrayer = createPermissionIfNotExists("PRAYER_DELETE");
        // Event permissions
        Permission readEvent = createPermissionIfNotExists("EVENT_READ");
        Permission writeEvent = createPermissionIfNotExists("EVENT_WRITE");
        Permission editEvent = createPermissionIfNotExists("EVENT_EDIT");
        Permission deleteEvent = createPermissionIfNotExists("EVENT_DELETE");
        // Ministries permissions
        Permission readMinistry = createPermissionIfNotExists("MINISTRY_READ");
        Permission writeMinistry = createPermissionIfNotExists("MINISTRY_WRITE");
        Permission editMinistry = createPermissionIfNotExists("MINISTRY_EDIT");
        Permission deleteMinistry = createPermissionIfNotExists("MINISTRY_DELETE");
        // Register permissions
        Permission registerPermission = createPermissionIfNotExists("REGISTER_ACCESS");
        // Contact permissions
        Permission readContact = createPermissionIfNotExists("CONTACT_READ");
        Permission writeContact = createPermissionIfNotExists("CONTACT_WRITE");
        Permission editContact = createPermissionIfNotExists("CONTACT_EDIT");
        Permission deleteContact = createPermissionIfNotExists("CONTACT_DELETE");
        // Sermon Permissions
        Permission readSermon = createPermissionIfNotExists("SERMON_READ");
        Permission writeSermon = createPermissionIfNotExists("SERMON_WRITE");
        Permission editSermon = createPermissionIfNotExists("SERMON_EDIT");
        Permission deleteSermon = createPermissionIfNotExists("SERMON_DELETE");
        // SermonNote Permissions
        Permission readSermonNote = createPermissionIfNotExists("SERMONNOTE_READ");
        Permission writeSermonNote = createPermissionIfNotExists("SERMONNOTE_WRITE");
        Permission editSermonNote = createPermissionIfNotExists("SERMONNOTE_EDIT");
        Permission deleteSermonNote = createPermissionIfNotExists("SERMONNOTE_DELETE");

        // Crear rol USER con permisos de lectura
        createRoleIfNotExists("USER", 1, Set.of("NEWSLETTER_READ", "VIDEO_READ", "PRAYER_READ", "ATTENDANCE_READ" ,"MINISTRY_READ", "CONTACT_READ", "SERMON_READ", "EVENT_READ", "SERMONNOTE_READ"));

        // Crear rol ADMINISTRATOR con todos los permisos
        createRoleIfNotExists("ADMINISTRATOR", 4, Set.of(
                "ADMIN_ACCESS"
        ));
        // Crear cuenta de administrador si no existe
        String adminEmail = "admin@livingword.com";
        if (!appUserRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail(adminEmail);
            admin.setVerified(true);
            admin.setGender("male");
            admin.setLastname("Engage");
            admin.setPassword(passwordEncoder.encode("admin12345")); // **Cambiar a una contraseña segura**
            Role adminRole = roleService.getRoleByName("ADMINISTRATOR")
                    .orElseThrow(() -> new IllegalStateException("ADMINISTRATOR role not found"));
            admin.setRole(adminRole);
            appUserRepository.save(admin);
            System.out.println("Cuenta de administrador creada con email: " + adminEmail + " y contraseña: admin123");
        }
    }

    private Permission createPermissionIfNotExists(String permissionName) {
        try {
            return permissionService.getPermissionByName(permissionName);
        } catch (Exception e) {
            return permissionService.createPermission(permissionName);
        }
    }

    private void createRoleIfNotExists(String roleName, int level, Set<String> permissions) {
        if (!roleService.getRoleByName(roleName).isPresent()) {
            roleService.createRole(roleName, level, permissions);
        }
    }
}