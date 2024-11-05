package living.word.livingword.model.dto;

import lombok.Data;

@Data
public class SermonAttendanceStats {
    private Long sermonId;
    private long attended;
    private long notAttended;
    private long noResponse;
}