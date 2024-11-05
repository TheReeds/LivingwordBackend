package living.word.livingword.repository;

import living.word.livingword.entity.Ministry;
import living.word.livingword.entity.Role;
import living.word.livingword.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByMinistry(Ministry ministry);
    List<User> findByRole(Role role);
}
