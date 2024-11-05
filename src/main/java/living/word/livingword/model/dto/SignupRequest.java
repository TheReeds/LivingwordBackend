package living.word.livingword.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data   
public class SignupRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Last Name is mandatory")
    private String lastname;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password is mandatory")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;
    @NotBlank(message = "Phone is mandatory")
    private String phone;
    @NotBlank(message = "Address is mandatory")            // Número de teléfono
    private String address;      // Dirección
    private LocalDate dateBirth; 
    @NotBlank(message = "dateBirth is mandatory")          
    private String maritalstatus;  
    @NotBlank(message = "Gender is mandatory")       // Estado civil (ej. Soltero, Casado)
    private String gender;
    @NotBlank(message = "Role is mandatory")
    private String role; // "USER", "DEPARTMENT_LEADER", "SECRETARY", "ADMINISTRATOR"
}