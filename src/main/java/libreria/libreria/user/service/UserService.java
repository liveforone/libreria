package libreria.libreria.user.service;

import libreria.libreria.jwt.JwtTokenProvider;
import libreria.libreria.jwt.TokenInfo;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.dto.UserResponse;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import libreria.libreria.user.util.UserMapper;
import libreria.libreria.user.util.UserUtils;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public Users getUserEntity(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse getUserDto(String email) {
        Users users = userRepository.findByEmail(email);

        if (CommonUtils.isNull(users)) {
            return null;
        }

        return UserMapper.dtoBuilder(users);
    }

    /*
    * 모든 유저 반환
    * when : 권한이 어드민인 유저가 호출할때
     */
    public List<Users> getAllUsersForAdmin() {
        return userRepository.findAll();
    }

    @Transactional
    public void signup(UserRequest userRequest) {
        userRequest.setPassword(
                UserUtils.encodePassword(userRequest.getPassword())
        );

        if (Objects.equals(userRequest.getEmail(), "admin@libreria.com")) {
            userRequest.setAuth(Role.ADMIN);
        } else {
            userRequest.setAuth(Role.MEMBER);
        }

        userRepository.save(
                UserMapper.dtoToEntity(userRequest)
        );
    }

    @Transactional
    public TokenInfo login(UserRequest userRequest) {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,
                password
        );
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(authenticationToken);

        return jwtTokenProvider
                .generateToken(authentication);
    }

    @Transactional
    public void updateAuth (String email) {
        UserUtils.updateContextHolderAuth();
        userRepository.updateUserAuth(Role.SELLER, email);
    }

    @Transactional
    public void regiAddress(String email, String address) {
        userRepository.updateAddress(address, email);
    }

    @Transactional
    public void updateEmail(String oldEmail, String newEmail) {
        userRepository.updateEmail(oldEmail, newEmail);
    }

    @Transactional
    public void updatePassword(Long id, String inputPassword) {
        String newPassword = UserUtils.encodePassword(inputPassword);

        userRepository.updatePassword(id, newPassword);
    }

    @Transactional
    public void plusCount(String email) {
        userRepository.plusCount(email);
    }

    @Transactional
    public void minusCount(String email) {
        userRepository.minusCount(email);
    }

    @Transactional
    public void degrade(String email) {
        userRepository.degrade(email);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
