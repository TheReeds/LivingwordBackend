package living.word.livingword.service;

import living.word.livingword.entity.Sermon;
import living.word.livingword.repository.SermonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledTaskService {

    @Autowired
    private SermonRepository sermonRepository;

    @Autowired
    private SermonService sermonService;

    // Ejecutar cada minuto
    @Scheduled(cron = "0 * * * * *")
    public void checkAndStartEndSermons() {
        LocalDateTime now = LocalDateTime.now();

        // Iniciar sermones cuya startTime <= now y no activos
        List<Sermon> sermonsToStart = sermonRepository.findAll().stream()
                .filter(sermon -> !sermon.isActive() &&
                        sermon.getStartTime().isBefore(now.plusSeconds(1)) &&
                        sermon.getEndTime().isAfter(now))
                .collect(Collectors.toList());

        for(Sermon sermon : sermonsToStart){
            sermonService.startSermon(sermon.getId());
        }

        // Finalizar sermones cuya endTime <= now y activos
        List<Sermon> sermonsToEnd = sermonRepository.findAll().stream()
                .filter(sermon -> sermon.isActive() &&
                        sermon.getEndTime().isBefore(now.plusSeconds(1)))
                .collect(Collectors.toList());

        for(Sermon sermon : sermonsToEnd){
            sermonService.endSermon(sermon.getId());
        }
    }
}