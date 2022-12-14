package libreria.libreria.user.service;

import libreria.libreria.user.model.Role;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return createUserDetails(userRepository.findByEmail(email));
    }

    private UserDetails createUserDetails(Users users) {
        if (users.getAuth() == Role.ADMIN) {
            return User.builder()
                    .username(users.getEmail())
                    .password(users.getPassword())
                    .roles("ADMIN")
                    .build();
        } else if (users.getAuth() == Role.SELLER) {
            return User.builder()
                    .username(users.getEmail())
                    .password(users.getPassword())
                    .roles("SELLER")
                    .build();
        } else {
            return User.builder()
                    .username(users.getEmail())
                    .password(users.getPassword())
                    .roles("MEMBER")
                    .build();
        }
    }
}
