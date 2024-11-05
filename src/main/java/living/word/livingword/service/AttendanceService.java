package living.word.livingword.service;

import living.word.livingword.entity.Attendance;
import living.word.livingword.entity.Sermon;
import living.word.livingword.entity.User;
import living.word.livingword.repository.AttendanceRepository;
import living.word.livingword.repository.SermonRepository;
import living.word.livingword.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private SermonRepository sermonRepository;

    @Autowired
    private NotificationService notificationService;

    // Registrar asistencia y feedback
    @Transactional
    public void recordAttendance(boolean attended, Integer rating, String feedback) {
        User user = getCurrentUser();  // Usar el método para obtener el usuario actual

        // Obtener el sermón activo
        Sermon activeSermon = sermonRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalArgumentException("No hay un sermón activo"));

        // Verificar si ya se registró asistencia para este sermón
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserAndSermon(user, activeSermon);
        if (existingAttendance.isPresent()) {
            throw new IllegalStateException("Ya has registrado tu asistencia para este sermón.");
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setSermon(activeSermon);
        attendance.setDate(LocalDateTime.now());
        attendance.setAttended(attended);
        attendance.setRating(attended ? rating : null);
        attendance.setFeedback(attended ? feedback : null);

        attendanceRepository.save(attendance);

        /*if (!attended) {
            // Notificar a los administradores sobre la ausencia
            notificationService.notifyAdminAbsence(user, activeSermon);
        }*/
    }

    // Obtener registros de asistencia de un usuario
    public List<Attendance> getUserAttendance() {
        User user = getCurrentUser();  // Usar el metodo para obtener el usuario actual

        // Busca la asistencia por el usuario autenticado
        return attendanceRepository.findByUser(user);
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No se encontró un usuario autenticado");
    }
}
