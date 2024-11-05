package living.word.livingword.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Ministry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "ministry", cascade = CascadeType.ALL)
    private List<Event> events;

    @OneToMany(mappedBy = "ministry", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<User> users;
}