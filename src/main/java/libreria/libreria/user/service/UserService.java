package libreria.libreria.user.service;

import libreria.libreria.user.model.Role;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.dto.UserResponse;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import libreria.libreria.user.util.UserConstants;
import libreria.libreria.user.util.UserMapper;
import libreria.libreria.user.util.UserUtils;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /*
    * 이메일 중복 검증
    * 반환 값 : 1(중복아님), 0(중복)
     */
    public int checkDuplicateEmail(String email) {

        Users users = userRepository.findByEmail(email);

        if (CommonUtils.isNull(users)) {
            return UserConstants.NOT_DUPLICATE.getValue();
        }
        return UserConstants.DUPLICATE.getValue();
    }

    public Users getUserEntity(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse getUserDto(String email) {
        Users users = userRepository.findByEmail(email);

        if (CommonUtils.isNull(users)) {
            return null;
        }

        //user 등급 체크
        String rank = UserUtils.rankCheck(users.getCount());

        return UserResponse.builder()
                .id(users.getId())
                .email(users.getEmail())
                .address(users.getAddress())
                .rank(rank)
                .auth(users.getAuth())
                .build();
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
        userRequest.setAuth(Role.MEMBER);

        userRepository.save(
                UserMapper.dtoToEntity(userRequest)
        );
    }

    @Transactional
    public void login(UserRequest userRequest, HttpSession httpSession)
            throws UsernameNotFoundException
    {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        Users user = userRepository.findByEmail(email);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(token);
        httpSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        List<GrantedAuthority> authorities = new ArrayList<>();
        /*
        * 처음 어드민이 로그인을 하는경우 이메일로 판별해서 권한을 admin 으로 변경해주고
        * 그 다음부터 어드민이 업데이트 할때에는 auth 칼럼으로 판별해서 db 업데이트 하지않고,
        * GrantedAuthority 만 업데이트 해준다.
         */
        if (user.getAuth() != Role.ADMIN && ("admin@libreria.com").equals(email)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
            userRepository.updateAuth(Role.ADMIN, userRequest.getEmail());
        }

        if (user.getAuth() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        }

        if (user.getAuth() == Role.SELLER) {
            authorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));

        new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException
    {
        Users users = userRepository.findByEmail(email);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (users.getAuth() == Role.ADMIN) {  //어드민 아이디 지정됨, 비밀번호는 회원가입해야함
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        }

        if (users.getAuth() == Role.SELLER) {
            authorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));

        return new User(
                users.getEmail(),
                users.getPassword(),
                authorities
        );
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

        userRepository.updateAuth(
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
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
