package libreria.libreria.user.controller;

import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.service.OrderService;
import libreria.libreria.user.dto.UserChangeEmailRequest;
import libreria.libreria.user.dto.UserChangePasswordRequest;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.dto.UserResponse;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.service.UserService;
import libreria.libreria.utility.CommonUtils;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;

    //== ------ 상수 선언 부 ------ ==//
    private static final int NOT_DUPLICATE = 1;
    private static final int PASSWORD_MATCH = 1;


    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok("home");
    }

    @GetMapping("/user/signup")
    public ResponseEntity<?> signupPage() {
        return ResponseEntity.ok("회원가입페이지");
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequest userRequest) {

        int checkEmail =
                userService.checkDuplicateEmail(userRequest.getEmail());

        if (checkEmail != NOT_DUPLICATE) {
            return ResponseEntity.ok("중복되는 이메일이 있어 회원가입이 불가능합니다.");

        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));

        userService.joinUser(userRequest);
        log.info("회원 가입 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/user/login")
    public ResponseEntity<?> loginPage() {
        return ResponseEntity.ok("로그인 페이지");
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginPage(
            @RequestBody UserRequest userRequest,
            HttpSession session
    ) {
        Users users = userService.getUserEntity(userRequest.getEmail());

        if (CommonUtils.isNull(users)) {
            return ResponseEntity.ok("회원 조회가 되지않아 로그인이 불가능합니다.");
        }

        int checkPassword = userService.checkPasswordMatching(
                        userRequest.getPassword(),
                        users.getPassword()
        );

        if (checkPassword != PASSWORD_MATCH) {
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 시도하세요.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));

        userService.login(
                userRequest,
                session
        );
        log.info("로그인 성공!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    /*
    로그아웃은 시큐리티 단에서 이루어짐.
    /user/logout 으로 post 하면 된다.
     */

    @PostMapping("/user/change-email")
    public ResponseEntity<?> changeEmail(
            @RequestBody UserChangeEmailRequest userRequest,
            Principal principal
    ) {
        Users users = userService.getUserEntity(principal.getName());
        Users duplicateUser = userService.getUserEntity(userRequest.getEmail());

        if (CommonUtils.isNull(users)) {
            return ResponseEntity
                    .ok("해당 유저를 조회할 수 없어 이메일 변경이 불가능합니다.");
        }

        if (!CommonUtils.isNull(duplicateUser)) {
            return ResponseEntity
                    .ok("해당 이메일이 이미 존재합니다. 다시 입력해주세요");
        }

        int checkPassword = userService.checkPasswordMatching(
                        userRequest.getPassword(),
                        users.getPassword()
        );

        if (checkPassword != PASSWORD_MATCH) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/user/logout"
        ));

        userService.updateEmail(
                principal.getName(),
                userRequest.getEmail()
        );
        log.info("이메일 변경 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    //== 비밀번호 변경 ==//
    @PostMapping("/user/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody UserChangePasswordRequest userRequest,
            Principal principal
    ) {
        Users users = userService.getUserEntity(principal.getName());

        if (CommonUtils.isNull(users)) {
            return ResponseEntity
                    .ok("해당 유저를 조회할 수 없어 비밀번호 변경이 불가능합니다.");
        }

        int checkPassword = userService.checkPasswordMatching(
                        userRequest.getOldPassword(),
                        users.getPassword()
        );

        if (checkPassword != PASSWORD_MATCH) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/user/logout"
        ));

        userService.updatePassword(
                users.getId(),
                userRequest.getNewPassword()
        );
        log.info("비밀번호 변경 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    //== 회원 탈퇴 ==//
    @PostMapping("/user/withdraw")
    public ResponseEntity<?> userWithdraw(
            @RequestBody String password,
            Principal principal
    ) {
        Users users = userService.getUserEntity(principal.getName());

        if (CommonUtils.isNull(users)) {
            return ResponseEntity.ok("해당 유저를 조회할 수 없어 탈퇴가 불가능합니다.");
        }

        int checkPassword = userService.checkPasswordMatching(
                password,
                users.getPassword()
        );

        if (checkPassword != PASSWORD_MATCH) {
            log.info("비밀번호 일치하지 않음.");
            return ResponseEntity.ok("비밀번호가 다릅니다. 다시 입력해주세요.");
        }

        log.info("회원 : " + users.getId() + " 탈퇴 성공!!");
        userService.deleteUser(users.getId());

        return ResponseEntity.ok("그동안 서비스를 이용해주셔서 감사합니다.");
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

        userService.updateAuth(principal.getName());
        log.info("seller 권한 업데이트 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    /*
    README에서도 설명했지만 화면단에서 auth를 바탕으로
    MEMBER일 경우 주문 리스트 버튼을 띄어서 /user/orderlist로 연결하고
    SELLER일 경우 등록 상품 버튼을 띄어서 /user/itemlist 로 연결한다.
     */
    @GetMapping("/user/my-page")  //rest-api에서는 대문자를 쓰지않는다.
    public ResponseEntity<?> myPage(Principal principal) {
        UserResponse dto = userService.getUserDto(principal.getName());

        return ResponseEntity.ok(
                Objects.requireNonNullElse(
                        dto,
                        "회원님을 조회할 수 없어 회원님 정보제공이 불가능합니다."
                )
        );
    }

    //== 주소 등록 페이지 ==//
    @GetMapping("/user/address")
    public ResponseEntity<?> regiAddressPage(Principal principal) {
        String address = userService.getUserEntity(principal.getName()).getAddress();

        return ResponseEntity.ok(address);
    }

    //== 주소 등록 ==//
    @PostMapping("/user/address")
    public ResponseEntity<?> regiAddress(
            @RequestBody String address,
            Principal principal
    ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/user/my-page"
        ));
        String user = principal.getName();

        userService.regiAddress(user, address);
        log.info("주소 등록 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    //== 내가 등록한 상품 - 권한이 판매자일 경우 ==//
    @GetMapping("/user/item-list")
    public ResponseEntity<?> myItemList(Principal principal) {
        Users users = userService.getUserEntity(principal.getName());

        if (users.getAuth() != Role.SELLER) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ItemResponse> itemList =
                itemService.getItemListForMyPage(users.getEmail());
        return ResponseEntity.ok(itemList);
    }

    //== 내가 주문한 상품 - 권한이 멤버일 경우 ==//
    @GetMapping("/user/order-list")
    public ResponseEntity<?> myOrderList(Principal principal) {
        Users users = userService.getUserEntity(principal.getName());

        if (users.getAuth() != Role.MEMBER) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        List<OrdersResponse> ordersList =
                orderService.getOrderListForMyPage(users.getEmail());
        return ResponseEntity.ok(ordersList);
    }

    //== 접근 거부 페이지 ==//
    @GetMapping("/user/prohibition")
    public ResponseEntity<?> prohibition() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("접근 권한이 없습니다.");
    }

    //== 어드민 페이지 ==//
    @GetMapping("/admin")
    public ResponseEntity<?> admin(Principal principal) {
        Users users = userService.getUserEntity(principal.getName());

        if (!users.getAuth().equals(Role.ADMIN)) {  //권한 검증
            log.info("어드민 페이지 접속에 실패했습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("어드민이 어드민 페이지에 접속했습니다.");
        return ResponseEntity.ok(userService.getAllUsersForAdmin());
    }
}
