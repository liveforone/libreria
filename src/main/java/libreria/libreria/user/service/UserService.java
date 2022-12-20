package libreria.libreria.user.service;

import libreria.libreria.jwt.JwtTokenProvider;
import libreria.libreria.jwt.TokenInfo;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.dto.UserResponse;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import libreria.libreria.user.util.UserMapper;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public void joinUser(UserRequest userRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userRequest.setPassword(passwordEncoder.encode(
                userRequest.getPassword()
        ));

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
        /*
        * 권한을 업데이트(컨텍스트홀더 + db)
        * 업데이트 한 권한 현재 객체에 저장, 로그아웃 하지 않아도 됨!
         */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities =
                new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        auth.getPrincipal(),
                        auth.getCredentials(),
                        updatedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        //컨텍스트홀더 업데이트 끝.

        userRepository.updateUserAuth(
                Role.SELLER,
                email
        );
    }

    @Transactional
    public void regiAddress(String email, String address) {
        userRepository.updateAddress(
                address,
                email
        );
    }

    @Transactional
    public void updateEmail(String oldEmail, String newEmail) {
        userRepository.updateEmail(
                oldEmail,
                newEmail
        );
    }

    @Transactional
    public void updatePassword(Long id, String inputPassword) {
        //pw 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);

        userRepository.updatePassword(
                id,
                newPassword
        );
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
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
