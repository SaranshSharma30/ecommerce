package UserService.repositories;

import UserService.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepo extends JpaRepository<Role, Long> {


    List<Role> findAllByIdIn(List<Long> roleIds);
}