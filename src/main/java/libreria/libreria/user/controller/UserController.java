package libreria.libreria.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.jwt.JwtAuthenticationFilter;
import libreria.libreria.jwt.TokenInfo;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.service.OrderService;
import libreria.libreria.user.dto.UserChangeEmailRequest;
import libreria.libreria.user.dto.UserChangePasswordRequest;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.dto.UserResponse;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.service.UserService;
import libreria.libreria.user.util.UserEmail;
import libreria.libreria.user.util.UserPassword;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("home");
    }

    @GetMapping("/user/signup")
    public ResponseEntity<?> signupPage() {
        return ResponseEntity.ok("회원가입페이지");
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> signup(
            @RequestBody UserRequest userRequest,
            HttpServletRequest request
    ) {
        if (UserEmail.isDuplicateEmail(
                userService.getUserEntity(userRequest.getEmail())
        )) {
            log.info("이메일이 중복됨.");
            return ResponseEntity.ok("중복되는 이메일이 있어 회원가입이 불가능합니다.");
        }

        userService.signup(userRequest);
        log.info("회원 가입 성공");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(makeHttpHeadersWhenSignup(request))
                .build();
    }

    private HttpHeaders makeHttpHeadersWhenSignup(HttpServletRequest request) {
        String url = "/user/login";
        String token = JwtAuthenticationFilter.resolveToken(request);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.setLocation(URI.create(url));
        return httpHeaders;
    }

    @GetMapping("/user/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok("로그인 페이지");
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        Users users = userService.getUserEntity(userRequest.getEmail());

        if (CommonUtils.isNull(users)) {
            log.info("잘못된 이메일.");
            return ResponseEntity.ok("회원 조회가 되지않아 로그인이 불가능합니다.");
        }

        if (UserPassword.isNotMatchingPassword(
                userRequest.getPassword(),
                users.getPassword()
        )) {
            log.info("비밀번호가 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 시도하세요.");
        }

        TokenInfo tokenInfo = userService.login(userRequest);
        log.info("로그인 성공");

        return ResponseEntity.ok(tokenInfo);
    }

    @GetMapping("/user/seller")
    public ResponseEntity<?> sellerPage() {
        return ResponseEntity.ok("판매자 등록 페이지");
    }

    @PostMapping("/user/seller")
    public ResponseEntity<?> seller(
            Principal principal,
            HttpServletRequest request
    ) {
        userService.updateAuth(principal.getName());
        log.info("seller 권한 업데이트 성공!!");

        String url = "/user/logout";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PutMapping("/user/change-email")
    public ResponseEntity<?> changeEmail(
            @RequestBody UserChangeEmailRequest userRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Users users = userService.getUserEntity(principal.getName());
        Users requestUsers = userService.getUserEntity(userRequest.getEmail());

        if (UserEmail.isDuplicateEmail(requestUsers)) {
            log.info("이메일이 중복됨.");
            return ResponseEntity
                    .ok("해당 이메일이 이미 존재합니다. 다시 입력해주세요");
        }

        if (UserPassword.isNotMatchingPassword(
                userRequest.getPassword(),
                users.getPassword()
        )) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        userService.updateEmail(
                principal.getName(),
                userRequest.getEmail()
        );
        log.info("이메일 변경 성공");

        String url = "/user/logout";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }


    @PutMapping("/user/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody UserChangePasswordRequest userRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Users users = userService.getUserEntity(principal.getName());

        if (UserPassword.isNotMatchingPassword(
                userRequest.getOldPassword(),
                users.getPassword()
        )) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        userService.updatePassword(
                users.getId(),
                userRequest.getNewPassword()
        );
        log.info("비밀번호 변경 성공");

        String url = "/user/logout";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/user/regi-address")
    public ResponseEntity<?> regiAddressPage(Principal principal) {
        String email = principal.getName();
        String address = userService.getUserEntity(email).getAddress();

        return ResponseEntity.ok(address);
    }

    @PostMapping("/user/regi-address")
    public ResponseEntity<?> regiAddress(
            @RequestBody String address,
            Principal principal,
            HttpServletRequest request
    ) {
        String user = principal.getName();

        userService.regiAddress(user, address);
        log.info("주소 등록 성공!!");

        String url = "/user/my-page";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    /*
    * README 에서도 설명했지만 화면단에서 auth 를 바탕으로
    * MEMBER 일 경우 주문 리스트 버튼을 띄어서 /user/order-list 로 연결하고
    * SELLER 일 경우 등록 상품 버튼을 띄어서 /user/item-list 로 연결한다.
     */
    @GetMapping("/user/my-page")
    public ResponseEntity<?> myPage(Principal principal) {
        UserResponse users = userService.getUserDto(principal.getName());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/item-list")
    public ResponseEntity<?> myItemList(Principal principal) {
        Users users = userService.getUserEntity(principal.getName());

        if (!users.getAuth().equals(Role.SELLER)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ItemResponse> items =
                itemService.getItemsForMyPage(users.getEmail());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/user/order-list")
    public ResponseEntity<?> myOrderList(Principal principal) {
        Users users = userService.getUserEntity(principal.getName());

        if (!users.getAuth().equals(Role.MEMBER)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        List<OrdersResponse> orderDtos =
                orderService.getOrdersForMyPage(users.getEmail());
        return ResponseEntity.ok(orderDtos);
    }

    @DeleteMapping("/user/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestBody String inputPassword,
            Principal principal
    ) {
        Users users = userService.getUserEntity(principal.getName());

        if (UserPassword.isNotMatchingPassword(
                inputPassword,
                users.getPassword()
        )) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        log.info("회원 : " + users.getId() + " 탈퇴 성공");
        userService.deleteUser(users.getId());

        return ResponseEntity.ok("그동안 서비스를 이용해주셔서 감사합니다.");
    }

    @GetMapping("/admin")
    public ResponseEntity<?> admin(Principal principal) {
        Users users = userService.getUserEntity(principal.getName());

        if (!users.getAuth().equals(Role.ADMIN)) {  //권한 검증
            log.info("어드민 페이지 접속에 실패했습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("어드민이 어드민 페이지에 접속했습니다.");
        return ResponseEntity.ok(
                userService.getAllUsersForAdmin()
        );
    }

    @GetMapping("/user/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("접근 권한이 없습니다.");
    }
}
