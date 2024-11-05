package living.word.livingword.repository;

import living.word.livingword.entity.Attendance;
import living.word.livingword.entity.Sermon;
import living.word.livingword.entity.User;
import living.word.livingword.model.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Obtener registros de asistencia de un usuario específico
    List<Attendance> findByUserId(Long userId);

    // Obtener registros de asistencia por estado (No Respondió, Asistió, No Asistió)
    List<Attendance> findByStatus(AttendanceStatus status);

    // Obtener registros de asistencia en una fecha específica
    List<Attendance> findByDate(LocalDateTime date);

    Optional<Attendance> findByUserAndSermon(User user, Sermon activeSermon);

    List<Attendance> findByUser(User user);
}