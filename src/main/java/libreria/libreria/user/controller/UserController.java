package libreria.libreria.user.controller;

import libreria.libreria.user.model.Role;
import libreria.libreria.user.model.UserDto;
import libreria.libreria.user.model.UserResponseDto;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    //== 메인 페이지 ==//
    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("home");
    }

    //== 회원가입 페이지 ==//
    @GetMapping("/user/signup")
    public ResponseEntity<?> signupPage() {
        return ResponseEntity.ok("회원가입페이지");
    }

    //== 회원가입 처리 ==//
    @PostMapping("/user/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));  //해당 경로로 리다이렉트

        userService.joinUser(userDto);
        log.info("회원 가입 성공!!");

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(httpHeaders).build();
    }

    //== 로그인 페이지 ==//
    @GetMapping("/user/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok("로그인 페이지");
    }

    //== 로그인 ==//
    @PostMapping("/user/login")
    public ResponseEntity<?> loginPage(
            @RequestBody UserDto userDto,
            HttpSession session
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));

        userService.login(userDto, session);
        log.info("로그인 성공!");

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(httpHeaders).build();
    }

    //== 판매자 등록 페이지 ==//
    @GetMapping("/user/seller")
    public ResponseEntity<?> sellerPage() {
        return ResponseEntity.ok("판매자 등록 페이지");
    }

    //== 판매자 등록 - 권한 업데이트 ==//
    @PostMapping("/user/seller")
    public ResponseEntity<?> seller(Principal principal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));
        String email = principal.getName();

        userService.updateAuth(email);
        log.info("seller 권한 업데이트 성공!!");

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(httpHeaders).build();
    }

    /*
    로그아웃은 시큐리티 단에서 이루어짐.
    /user/logout 으로 post 하면 된다.
     */

    /*
    README에서도 설명했지만 화면단에서 auth를 바탕으로
    MEMBER일 경우 주문 리스트 버튼을 띄어서 /user/orderlist로 연결하고
    SELLER일 경우 등록 상품 버튼을 띄어서 /user/itemlist 로 연결한다.
     */
    @GetMapping("/user/mypage")  //rest-api에서는 대문자를 쓰지않는다.
    public ResponseEntity<UserResponseDto> myPage(Principal principal) {
        String user = principal.getName();
        UserResponseDto dto = userService.getUser(user);

        return ResponseEntity.ok(dto);
    }

    //== 주소 등록 페이지 ==//
    @GetMapping("/user/address")
    public ResponseEntity<?> regiAddressPage(Principal principal) {
        String user = principal.getName();
        String address = userService.getUser(user).getAddress();

        return ResponseEntity.ok(address);
    }

    //== 주소 등록 ==//
    @PostMapping("/user/address")
    public ResponseEntity<?> regiAddress(
            @RequestBody String address,
            Principal principal
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/user/mypage"));
        String user = principal.getName();

        userService.regiAddress(user, address);
        log.info("주소 등록 성공!!");

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(httpHeaders).build();
    }

    //== 접근 거부 페이지 ==//
    @GetMapping("/user/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
    }

    //== 어드민 페이지 ==//
    @GetMapping("/admin")
    public ResponseEntity<?> admin(Principal principal) {
        String user = principal.getName();
        UserResponseDto dto = userService.getUser(user);
        if (dto.getAuth().equals(Role.ADMIN)) {  //권한 검증
            log.info("어드민이 어드민 페이지에 접속했습니다.");
            return ResponseEntity.ok(userService.getAllUsersForAdmin());
        } else {
            log.info("어드민 페이지 접속에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
