package living.word.livingword.controller;

import living.word.livingword.entity.MinistrySurvey;
import living.word.livingword.model.dto.MinistrySurveyDto;
import living.word.livingword.model.dto.MinistryWithResponseDto;
import living.word.livingword.service.MinistrySurveyService;
import living.word.livingword.service.MinistrySurveyService.SurveyResponseRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/ministries/surveys"))
public class MinistrySurveyController {

    @Autowired
    private MinistrySurveyService ministrySurveyService;

    @PostMapping("/responses")
    public void recordSurveyResponses(@RequestBody List<SurveyResponseRequest> surveyResponses) {
        ministrySurveyService.recordSurveyResponses(surveyResponses);
    }

    @GetMapping("/{ministryId}/responses")
    public List<MinistrySurveyDto> getSurveyResponsesByMinistry(@PathVariable Long ministryId) {
        return ministrySurveyService.getSurveyResponsesByMinistry(ministryId);
    }

    @GetMapping("/{ministryId}/responses/{response}")
    public List<MinistrySurveyDto> getSurveyResponsesByMinistryAndResponse(@PathVariable Long ministryId, @PathVariable String response) {
        MinistrySurvey.ParticipationResponse participationResponse = MinistrySurvey.ParticipationResponse.valueOf(response.toUpperCase());
        return ministrySurveyService.getSurveyResponsesByMinistryAndResponse(ministryId, participationResponse);
    }

    @GetMapping("/{ministryId}/statistics")
    public MinistrySurveyService.StatisticsDto getSurveyStatistics(@PathVariable Long ministryId) {
        return ministrySurveyService.getSurveyStatistics(ministryId);
    }
    @GetMapping("/with-responses")
    public List<MinistryWithResponseDto> getMinistriesWithUserResponses() {
        return ministrySurveyService.getMinistriesWithUserResponses();
    }
}