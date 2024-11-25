package living.word.livingword.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import living.word.livingword.entity.Ministry;
import living.word.livingword.entity.MinistrySurvey;
import living.word.livingword.entity.User;
import living.word.livingword.exception.ResourceNotFoundException;
import living.word.livingword.model.dto.MinistrySurveyDto;
import living.word.livingword.model.dto.MinistryWithResponseDto;
import living.word.livingword.repository.AppUserRepository;
import living.word.livingword.repository.MinistryRepository;
import living.word.livingword.repository.MinistrySurveyRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
public class MinistrySurveyService {

    @Autowired
    private MinistrySurveyRepository ministrySurveyRepository;

    @Autowired
    private MinistryRepository ministryRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    public void recordSurveyResponses(List<SurveyResponseRequest> surveyResponses) {
        User currentUser = getCurrentUser();  // Obtener el usuario autenticado

        // Guardar o actualizar las respuestas para cada ministerio
        surveyResponses.forEach(response -> {
            Ministry ministry = ministryRepository.findById(response.getMinistryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ministerio no encontrado"));

            // Verificar si ya existe una respuesta del usuario para este ministerio
            MinistrySurvey existingSurvey = ministrySurveyRepository
                    .findByMinistryIdAndUserId(response.getMinistryId(), currentUser.getId());

            if (existingSurvey != null) {
                // Si ya existe una respuesta, la actualizamos
                existingSurvey.setResponse(response.getResponse());
                ministrySurveyRepository.save(existingSurvey);
            } else {
                // Si no existe respuesta, se registra una respuesta con "MAYBE" por defecto
                MinistrySurvey survey = new MinistrySurvey();
                survey.setMinistry(ministry);
                survey.setUser(currentUser);  // Asignar al usuario autenticado
                survey.setResponse(MinistrySurvey.ParticipationResponse.MAYBE);  // Respuesta por defecto
                ministrySurveyRepository.save(survey);
            }
        });
    }

    public List<MinistrySurveyDto> getSurveyResponsesByMinistry(Long ministryId) {
        List<MinistrySurvey> surveys = ministrySurveyRepository.findByMinistryId(ministryId);

        return surveys.stream().map(survey -> {
            MinistrySurveyDto dto = new MinistrySurveyDto();
            dto.setMinistryId(survey.getMinistry().getId());
            dto.setUserId(survey.getUser().getId());
            dto.setUserName(survey.getUser().getName());
            dto.setUserLastname(survey.getUser().getLastname());
            dto.setResponse(MinistrySurveyDto.ParticipationResponse.valueOf(survey.getResponse().name()));
            return dto;
        }).collect(Collectors.toList());
    }

    public List<MinistrySurveyDto> getSurveyResponsesByMinistryAndResponse(Long ministryId, MinistrySurvey.ParticipationResponse response) {
        List<MinistrySurvey> surveys = ministrySurveyRepository.findByMinistryIdAndResponse(ministryId, response);

        return surveys.stream().map(survey -> {
            MinistrySurveyDto dto = new MinistrySurveyDto();
            dto.setMinistryId(survey.getMinistry().getId());
            dto.setUserId(survey.getUser().getId());
            dto.setUserName(survey.getUser().getName());
            dto.setUserLastname(survey.getUser().getLastname());
            dto.setResponse(MinistrySurveyDto.ParticipationResponse.valueOf(survey.getResponse().name()));
            return dto;
        }).collect(Collectors.toList());
    }

    // Consultar estadísticas de respuestas (Yes, No, Maybe)
    public StatisticsDto getSurveyStatistics(Long ministryId) {
        List<MinistrySurvey> yesResponses = ministrySurveyRepository.findByMinistryIdAndResponse(ministryId, MinistrySurvey.ParticipationResponse.YES);
        List<MinistrySurvey> noResponses = ministrySurveyRepository.findByMinistryIdAndResponse(ministryId, MinistrySurvey.ParticipationResponse.NO);
        List<MinistrySurvey> maybeResponses = ministrySurveyRepository.findByMinistryIdAndResponse(ministryId, MinistrySurvey.ParticipationResponse.MAYBE);

        StatisticsDto statistics = new StatisticsDto();
        statistics.setYesCount(yesResponses.size());
        statistics.setNoCount(noResponses.size());
        statistics.setMaybeCount(maybeResponses.size());

        statistics.setYesUsers(yesResponses.stream()
                .map(survey -> survey.getUser().getName() + " " + survey.getUser().getLastname())
                .collect(Collectors.toList()));

        statistics.setNoUsers(noResponses.stream()
                .map(survey -> survey.getUser().getName() + " " + survey.getUser().getLastname())
                .collect(Collectors.toList()));

        statistics.setMaybeUsers(maybeResponses.stream()
                .map(survey -> survey.getUser().getName() + " " + survey.getUser().getLastname())
                .collect(Collectors.toList()));

        return statistics;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatisticsDto {
        private int yesCount;
        private int noCount;
        private int maybeCount;
        private List<String> yesUsers;
        private List<String> noUsers;
        private List<String> maybeUsers;

        // Getters and setters
        public int getYesCount() {
            return yesCount;
        }
    
        public void setYesCount(int yesCount) {
            this.yesCount = yesCount;
        }
    
        public int getNoCount() {
            return noCount;
        }
    
        public void setNoCount(int noCount) {
            this.noCount = noCount;
        }
    
        public int getMaybeCount() {
            return maybeCount;
        }
    
        public void setMaybeCount(int maybeCount) {
            this.maybeCount = maybeCount;
        }
    
        public List<String> getYesUsers() {
            return yesUsers;
        }
    
        public void setYesUsers(List<String> yesUsers) {
            this.yesUsers = yesUsers;
        }
    
        public List<String> getNoUsers() {
            return noUsers;
        }
    
        public void setNoUsers(List<String> noUsers) {
            this.noUsers = noUsers;
        }
    
        public List<String> getMaybeUsers() {
            return maybeUsers;
        }
    
        public void setMaybeUsers(List<String> maybeUsers) {
            this.maybeUsers = maybeUsers;
        }
    }
    public static class SurveyResponseRequest {
        private Long ministryId;
        private MinistrySurvey.ParticipationResponse response;
        
        public Long getMinistryId() {
            return ministryId;
        }

        public void setMinistryId(Long ministryId) {
            this.ministryId = ministryId;
        }

        public MinistrySurvey.ParticipationResponse getResponse() {
            return response;
        }

        public void setResponse(MinistrySurvey.ParticipationResponse response) {
            this.response = response;
        }
    }
    public List<MinistryWithResponseDto> getMinistriesWithUserResponses() {
    User currentUser = getCurrentUser();  // Obtener el usuario autenticado

    // Obtener todos los ministerios
    List<Ministry> ministries = ministryRepository.findAll();

    // Crear una lista de DTOs con ministerios y respuestas
    List<MinistryWithResponseDto> ministryWithResponses = new ArrayList<>();

    for (Ministry ministry : ministries) {
        MinistryWithResponseDto dto = new MinistryWithResponseDto();
        dto.setMinistryId(ministry.getId());
        dto.setMinistryName(ministry.getName());

        // Verificar si el usuario ya respondió a este ministerio
        MinistrySurvey existingResponse = ministrySurveyRepository.findByMinistryIdAndUserId(ministry.getId(), currentUser.getId());
        if (existingResponse != null) {
            dto.setResponse(existingResponse.getResponse().name());  // Asignar la respuesta del usuario
        } else {
            dto.setResponse(null);  // Si no tiene respuesta, la dejamos como null
        }


        ministryWithResponses.add(dto);
    }

    return ministryWithResponses;
}

}
