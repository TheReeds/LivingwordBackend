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
    private String description;

    @OneToMany(mappedBy = "ministry", cascade = CascadeType.ALL)
    private List<Event> events;
    @ManyToMany
    @JoinTable(name = "ministry_leaders", 
               joinColumns = @JoinColumn(name = "ministry_id"), 
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> leaders;

    @OneToMany(mappedBy = "ministry", cascade = CascadeType.ALL)
    private List<User> members;

    @OneToMany(mappedBy = "ministry", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<User> users;
}