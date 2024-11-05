package living.word.livingword.service;

import living.word.livingword.entity.Attendance;
import living.word.livingword.entity.Sermon;
import living.word.livingword.entity.User;
import living.word.livingword.exception.SermonNotFoundException;
import living.word.livingword.model.dto.SermonAttendanceStats;
import living.word.livingword.repository.SermonRepository;
import living.word.livingword.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SermonService {

    @Autowired
    private SermonRepository sermonRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private NotificationService notificationService;


    // Crear un sermo
    public Sermon createSermon(Sermon sermon) {
        // Solo ADMINISTRATOR o SECRETARY pueden crear sermones
        User currentUser = getCurrentUser();
        if (!(currentUser.getRole().getName().equalsIgnoreCase("ADMINISTRATOR") ||
                currentUser.getRole().getName().equalsIgnoreCase("SECRETARY"))) {
            throw new AccessDeniedException("No tienes permiso para crear sermones.");
        }

        sermon.setCreatedBy(currentUser);
        sermon.setActive(false); // Inicialmente no activo
        return sermonRepository.save(sermon);
    }

    // Actualizar un sermo
    public Sermon updateSermon(Long sermonId, Sermon updatedSermon) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new SermonNotFoundException("Sermon not found"));

        // Solo ADMINISTRATOR o SECRETARY pueden actualizar sermones
        User currentUser = getCurrentUser();
        if (!(currentUser.getRole().getName().equalsIgnoreCase("ADMINISTRATOR") ||
                currentUser.getRole().getName().equalsIgnoreCase("SECRETARY"))) {
            throw new AccessDeniedException("No tienes permiso para actualizar sermones.");
        }

        sermon.setTitle(updatedSermon.getTitle());
        sermon.setDescription(updatedSermon.getDescription());
        sermon.setStartTime(updatedSermon.getStartTime());
        sermon.setEndTime(updatedSermon.getEndTime());
        sermon.setVideoLink(updatedSermon.getVideoLink());
        sermon.setSummary(updatedSermon.getSummary());

        return sermonRepository.save(sermon);
    }

    // Obtener todos los sermones
    public List<Sermon> getAllSermons() {
        return sermonRepository.findAll();
    }

    // Obtener sermo por ID
    public Sermon getSermonById(Long sermonId) {
        return sermonRepository.findById(sermonId)
                .orElseThrow(() -> new SermonNotFoundException("Sermon not found"));
    }

    // Iniciar un sermo
    @Transactional
    public Sermon startSermon(Long sermonId) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new SermonNotFoundException("Sermon not found"));

        if (sermon.isActive()) {
            throw new IllegalStateException("El sermón ya está activo.");
        }

        // Desactivar cualquier sermo activo
        Optional<Sermon> activeSermonOpt = sermonRepository.findByActiveTrue();
        if (activeSermonOpt.isPresent()) {
            Sermon activeSermon = activeSermonOpt.get();
            activeSermon.setActive(false);
            sermonRepository.save(activeSermon);
        }

        sermon.setActive(true);
        sermonRepository.save(sermon);

        // Enviar notificación de inicio de sermo
        notificationService.sendSermonStartNotification(sermon);

        return sermon;
    }

    // Finalizar un sermo
    @Transactional
    public Sermon endSermon(Long sermonId) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new SermonNotFoundException("Sermon not found"));

        if (!sermon.isActive()) {
            throw new IllegalStateException("El sermón no está activo.");
        }

        sermon.setActive(false);
        sermonRepository.save(sermon);

        // Enviar notificación de finalización y solicitud de feedback
        notificationService.sendSermonEndNotification(sermon);

        return sermon;
    }

    // Obtener estadísticas de asistencia de un sermo
    public SermonAttendanceStats getSermonAttendanceStats(Long sermonId) {
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new SermonNotFoundException("Sermon not found"));

        List<Attendance> attendances = sermon.getAttendanceRecords();

        long attended = attendances.stream().filter(Attendance::isAttended).count();
        long notAttended = attendances.stream().filter(a -> !a.isAttended()).count();
        long noResponse = userRepository.count() - attended - notAttended;

        SermonAttendanceStats stats = new SermonAttendanceStats();
        stats.setSermonId(sermonId);
        stats.setAttended(attended);
        stats.setNotAttended(notAttended);
        stats.setNoResponse(noResponse);

        return stats;
    }

    // Metodo para obtener el usuario autenticado
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No se encontró un usuario autenticado");
    }
}