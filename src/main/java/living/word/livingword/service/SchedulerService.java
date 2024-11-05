package living.word.livingword.service;

import living.word.livingword.entity.Sermon;
import living.word.livingword.repository.SermonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
public class SchedulerService {

    @Autowired
    private SermonRepository sermonRepository;


    // Programar sermones cada sábado a las 12:30 pm
    @Scheduled(cron = "0 30 12 ? * SAT") // Cada sábado a las 12:30 pm
    public void scheduleWeeklySermon() {
        // Obtener la próxima fecha de sábado a las 12:30 pm
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
                .withHour(12).withMinute(30).withSecond(0).withNano(0);

        // Verificar si ya existe un sermo programado para esa fecha
        Optional<Sermon> sermonOpt = sermonRepository.findAll().stream()
                .filter(sermon -> sermon.getStartTime().isEqual(nextSaturday))
                .findFirst();

        if (!sermonOpt.isPresent()) {
            // Crear un nuevo sermo
            Sermon sermon = new Sermon();
            sermon.setTitle("Culto de la Semana");
            sermon.setDescription("Descripción del culto.");
            sermon.setStartTime(nextSaturday);
            sermon.setEndTime(nextSaturday.plusHours(1)); // Duración de 1 hora
            sermon.setActive(false);
            // sermon.setCreatedBy(adminUser); // Opcional: asignar creador

            sermonRepository.save(sermon);
            System.out.println("Sermo programado para: " + nextSaturday);
        }
    }
}