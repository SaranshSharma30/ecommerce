package UserService.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import UserService.clients.KafkaProducerClient;
import UserService.dtos.SendEmailMessageDto;
import UserService.dtos.UserDto;
import UserService.models.Role;
import UserService.models.Session;
import UserService.models.SessionStatus;
import UserService.models.User;
import UserService.repositories.SessionRepo;
import UserService.repositories.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.PostMapping;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private UserRepo userRepository;
    private SessionRepo sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private KafkaProducerClient kafkaProducerClient;
    private ObjectMapper objectMapper;

    public AuthService(UserRepo userRepository, SessionRepo sessionRepository ,
                       BCryptPasswordEncoder bCryptPasswordEncoder, KafkaProducerClient kafkaProducerClient,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaProducerClient = kafkaProducerClient;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<?> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("Email is not registered!!",HttpStatus.UNAUTHORIZED);
        }

        User user = userOptional.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>("Wrong username/ password",HttpStatus.UNAUTHORIZED);
        }

        String token="";

        MacAlgorithm alg = Jwts.SIG.HS384;
        SecretKey key = alg.key().build();


        Map<String, Object>  jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));


        token = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(key, alg)
                .compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);


        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);

        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return null;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        UserDto userDto = UserDto.from(savedUser);

     try {
            kafkaProducerClient.sendMessage("userSignUp", objectMapper.writeValueAsString(userDto));

            SendEmailMessageDto emailMessage = new SendEmailMessageDto();
            emailMessage.setTo(userDto.getEmail());
            emailMessage.setFrom("admin@scaler.com");
            emailMessage.setSubject("Welcome to Ecommerce");
            emailMessage.setBody("Thanks for creating an account. Team ecommerce.");
            kafkaProducerClient.sendMessage("sendEmail", objectMapper.writeValueAsString(emailMessage));
        } catch (Exception e) {
            System.out.println("Something has gone wrong");
        }
        return userDto;
    }

    public SessionStatus validate(String token, Long userId) {
        System.out.println("Validating token: " + token + " for userId: " + userId);
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return SessionStatus.ENDED;
        }

        Session session = sessionOptional.get();

        if (!session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.ENDED;
        }

        MacAlgorithm alg = Jwts.SIG.HS384;
        SecretKey key = alg.key().build();

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        if (!claimsJws.getPayload().get("email").equals(session.getUser().getEmail())) {
            return SessionStatus.ENDED;
        }
        if (claimsJws.getPayload().get("createdAt") == null) {
            return SessionStatus.ENDED;
        }
        if (claimsJws.getPayload().get("expiryAt") != null) {
            Date expiryAt = (Date) claimsJws.getPayload().get("expiryAt");
            if (expiryAt.before(new Date())) {
                return SessionStatus.ENDED;
            }
        }
        return SessionStatus.ACTIVE;
    }

}
