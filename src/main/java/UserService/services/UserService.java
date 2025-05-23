package UserService.services;

import UserService.dtos.UserDto;
import UserService.models.User;
import org.springframework.stereotype.Service;
import UserService.repositories.*;

import java.util.Optional;

@Service
public class UserService {
    private UserRepo userRepository;


    public UserService(UserRepo userRepository) {
        this.userRepository = userRepository;

    }

    public UserDto getUserDetails(Long userId) {
        System.out.println("I got the request");
        return new UserDto();
    }

    public UserDto setUserRoles(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);


        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }
}
