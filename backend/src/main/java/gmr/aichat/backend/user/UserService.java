package gmr.aichat.backend.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    public final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(String name,
                       String email,
                       UserStatus status){

        if (userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        User user = new User(
                name,
                email,
                status
        );

        return userRepository.save(user);
    }

    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with id: " + id));
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User updateStatus(Long id,
                             UserStatus status){
        User user = findById(id);

        user.setStatus(status);

        return user;
    }

}
