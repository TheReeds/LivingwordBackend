package living.word.livingword.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String name;
    private String lastname;
    private String phone;
    private String address;
    private String gender;
    private String maritalstatus;
}
