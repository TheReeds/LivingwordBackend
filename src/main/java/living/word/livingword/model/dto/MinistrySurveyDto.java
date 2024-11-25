package living.word.livingword.model.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinistrySurveyDto {
    private Long ministryId;
    private Long userId;
    private String userName;
    private String userLastname;
    private ParticipationResponse response;

    public enum ParticipationResponse {
        YES, NO, MAYBE
    }
}