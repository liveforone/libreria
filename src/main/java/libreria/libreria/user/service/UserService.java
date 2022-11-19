package libreria.libreria.user.service;

import libreria.libreria.user.model.Role;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.dto.UserResponse;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
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
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    //== 이메일 중복 검증 ==//
    @Transactional(readOnly = true)
    public int checkSameEmail(String email) {
        Users users = userRepository.findByEmail(email);

        if (users == null) {  //중복 아님
            return 1;
        } else {  //중복임
            return 0;
        }
    }

    //== 비밀번호 복호화 ==//
    public int passwordDecode(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public Users getUserEntity(String email) {
        return userRepository.findByEmail(email);
    }

    //== 등급체크 후 pw없이 유저 정보 가져오기 ==//
    @Transactional(readOnly = true)
    public UserResponse getUser(String email) {
        Users users = userRepository.findByEmail(email);

        if (users == null) {
            return null;
        }

        String rank;
        // 등급 체크
        if (users.getCount() >= 120) {
            rank = "DIA";
        } else if (users.getCount() >= 60) {
            rank = "PLATINUM";
        } else if (users.getCount() >= 30) {
            rank = "GOLD";
        } else if (users.getCount() >= 15) {
            rank = "SILVER";
        } else {
            rank = "BRONZE";
        }

        return UserResponse.builder()
                .id(users.getId())
                .email(users.getEmail())
                .address(users.getAddress())
                .rank(rank)
                .auth(users.getAuth())
                .build();
    }

    //== 전체 유저 리턴 for admin ==//
    @Transactional(readOnly = true)
    public List<Users> getAllUsersForAdmin() {
        return userRepository.findAll();
    }

    //== 회원 가입 로직 ==//
    @Transactional
    public Long joinUser(UserRequest userRequest) {
        //비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRequest.setAuth(Role.MEMBER);  //기본 권한 매핑

        return userRepository.save(userRequest.toEntity()).getId();
    }

    //== 로그인 - 세션과 컨텍스트홀더 사용 ==//
    @Transactional
    public void login(UserRequest userRequest, HttpSession httpSession) throws UsernameNotFoundException {

        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        Users user = userRepository.findByEmail(email);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(token);
        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        List<GrantedAuthority> authorities = new ArrayList<>();
        /*
        처음 어드민이 로그인을 하는경우 이메일로 판별해서 권한을 admin으로 변경해주고
        그 다음부터 어드민이 업데이트 할때에는 auth 칼럼으로 판별해서 db 업데이트 하지않고,
        grandtedauthority 만 업데이트 해준다.
         */
        if (user.getAuth() != Role.ADMIN && ("admin@libreria.com").equals(email)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
            userRepository.updateAuth(Role.ADMIN, userRequest.getEmail());
        } else if (user.getAuth() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else if (user.getAuth() == Role.MEMBER) {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        } else if (user.getAuth() == Role.SELLER) {
            authorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        }
        new User(user.getEmail(), user.getPassword(), authorities);
    }

    //== spring context 반환 메소드(필수) ==//
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = userRepository.findByEmail(email);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (users.getAuth() == Role.ADMIN) {  //어드민 아이디 지정됨, 비밀번호는 회원가입해야함
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else if (users.getAuth() == Role.MEMBER) {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        } else if (users.getAuth() == Role.SELLER) {
            authorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        }

        return new User(users.getEmail(), users.getPassword(), authorities);
    }

    //== 권한 업데이트 ==//
    @Transactional
    public void updateAuth (String email) {
        //권한을 업데이트(컨텍스트홀더 + db객체)
        //업데이트 한 권한 현재 객체에 저장, 로그아웃 하지 않아도 됨!
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        //컨텍스트홀더 업데이트 끝.

        //db업데이트
        userRepository.updateAuth(Role.SELLER, email);
    }

    //== 유저 주소 등록 ==//
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
        //pw 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);

        userRepository.updatePassword(id, newPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
