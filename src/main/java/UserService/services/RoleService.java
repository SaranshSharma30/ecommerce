package UserService.services;
import UserService.models.Role;
import UserService.repositories.RoleRepo;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepo roleRepository;

    public RoleService(RoleRepo roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name) {
        Role role = new Role();
        role.setRole(name);

        return roleRepository.save(role);
    }
}





