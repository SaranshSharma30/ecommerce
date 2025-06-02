package UserService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import UserService.models.Session;


public interface SessionRepo extends JpaRepository<Session, Long> {

    Optional<Session> findByTokenAndUser_Id(String token, Long userId);
}