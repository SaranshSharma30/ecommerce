package UserService.services;

import UserService.dtos.UserDto;
import UserService.models.Role;
import UserService.models.User;
import org.springframework.stereotype.Service;
import UserService.repositories.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private UserRepo userRepository;
    private RoleRepo roleRepository;

    public UserService(UserRepo userRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }

    public UserDto getUserDetails(Long userId) {
        System.out.println("I got the request");
        return new UserDto();
    }

    public UserDto setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        User savedUser = userRepository.save(user);
        user.setRoles(Set.copyOf(roles));
        return UserDto.from(savedUser);
    }
}
