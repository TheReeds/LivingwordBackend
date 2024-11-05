package living.word.livingword.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "app_user")
public class User implements UserDetails { // Implementar UserDetails
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastname;

    @Column(unique=true, nullable=false)
    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "ministry_id")
    @JsonBackReference
    private Ministry ministry;

    private LocalDate dateBirth;  // Fecha de nacimiento
    private String phone;            // Número de teléfono
    private String address;           // Dirección
    private String maritalstatus;         // Estado civil (ej. Soltero, Casado)
    private String gender;    

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attendance> attendanceRecords;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PrayerRequest> prayerRequests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceToken> deviceTokens;

    // Verification email, change password
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false; // Indica si el usuario ha verificado su correo

    private String verificationToken; // Almacena el token de verificación

    private String resetPasswordToken; // Token para restablecer contraseña (opcional)

    // Métodos de UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getPermissions().stream()
                .map(permission -> (GrantedAuthority) () -> "PERM_" + permission.getName())
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    // Otros métodos de UserDetails pueden retornar valores predeterminados

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
