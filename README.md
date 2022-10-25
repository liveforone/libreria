# libreria
> 온라인 서점

## 사용 기술 스택
* Language : Java17
* DB : MySql
* ORM : Spring Data Jpa
* Spring Security
* LomBok
* Gradle
* Spring Boot 2.7.4

## 설명
* 온라인 서점 사이트이다.
* rest-api서버이다.
* rest-api 서버이지만 클라이언트 단에서 어떻게 반응해야하는지에 대한 설계도 모두 적혀있다.
* 즉 화면단 까지 고려하여 설계하고 작성되었다.
* 결제로직은 분리했다. 즉 다루지 않았다.
* 결제로직 분리를 통해 결제로직에 치중하는 문제에서 벗어났다.
* 깃의 wiki에 사용기술들과 설명들에 대해 자세히 기록했다.  
[나의 위키](https://github.com/liveforone/libreria/wiki)

## 설계
* 유저는 ADMIN, MEMBER, SELLER 세 종류가 있다.
* 어드민페이지에서 모든 유저를 확인 가능하다.
* 멤버는 중고서적을 주문, 리뷰 등록이 가능하다.
* 상품등록시 SELLER 권한이 필요하다.
* 회원은 등급이 있다. UserService의 getUser() 함수에서 등급을 체크한다.
* 브론즈, 실버(주문건수 15건 이상), 골드(30건), 플래티넘(60건), 다이아(120건)
* 등급은 mypage에서 나의 등급과 리뷰에서 게시자의 등급을 볼 수 있다.
* 도서의 사진은 1개만 첨부 가능하다. 도서는 많은 상품 이미지를 사실 필요로 하지않는다.(여타 사이트들도 마찬가지)
* 또한 사진이 없다면 도서의 등록은 불가능하다.(forbidden)
* 게시글 마다 리뷰게시판이 존재한다.
* 게시글의 게시자는 주문 리스트를 보는 것이가능하다. mypage와 비슷한 로직이다.
* 상품을 주문 할 때마다 상품 주문 횟수는 증가한다.
* 카테고리와 상품 홈 모두 좋아요(good) 순으로 페이징 정렬한다.
* 주문을 취소할때에는 반드시 주문한지 7일 이내에 취소해야한다.
* 품절된 상품은 body에 품절 메세지를 보내준다.
* 게시자는 상품의 수정이 가능하며, 품절시 재고 등록도 수정으로 한다.(게시자 판별은 서버에서함)
* 여타 다른 사이트가 그렇듯 상품은 품절됬음 품절됬지 게시글을 삭제하는것은 불가능하다.(어드민은 가능)

# 나의 고민
## 어드민의 로그인
* 어드민은 회사에서 지정한 admin@libreria.com 라는 지정된 이메일을 사용한다.
* 따라서 처음에 로그인 시 저 아이디로 접근한다면 최초 회원가입 시 모든 유저는 MEMBER 권한을 부여받기때문에,
* ADMIN으로 db 업데이트 쿼리를 내보낸다.
* 이후에 해당 이메일로 로그인 할때 이미 업데이트 되어있는 칼럼을 또 업데이트하는 쿼리가 나가므로,
* 판별식을 이용해서 첫 로그인이 아닌경우 auth 칼럼 값을 판별해서 GrantedAuthority값만 넣어주도록 설계했다.

## user를 전송할때 고민
* user를 json으로 클라이언트 한테 전달할때 pw도 같이 전달이된다.
* 개인적으로 아무리 암호화된 pw이라도 클라이언트에 pw를 보내는것은 좋은 방법이 아니라는 생각이 들었다.
* 따라서 UserResponseDto를 만들어서 pw를 제외한 나머지 정보들을 넣었고,
* UserService에서 getUser() 함수를 호출할때 회원등급을 체크하고 dto를 전달하는 방식으로 바꾸었다.
* 다만 어드민이 어드민 페이지에서 호출하는 getAllUsersForAdmin() 메소드에서는 모든 Users 칼럼을 다 내보낸다.

## 수량 칼럼(int count)에 대한 고민
* User엔티티에는 count라는 칼럼이 있다.
* 이 칼럼은 유저가 주문한 수량을 저장한다.
* 연관관계를 통해 order가 몇개있는지 체크해도되겠지만
* 칼럼으로 바로 조회하는것이 성능상에 좋을것 같다는 생각이든다.
* 또한 주문을 삭제할때 예를들어 6개월에 한번씩 localdate로 파싱해서 6개월된 status가 order인 주문이 있다면 삭제한다고 가정할 수 있다.
* 이때 나의 주문을 모두 가져와서 수량을 체크한다면 나의 등급이 초기화된다.
* 따라서 이러한 현상방지도 가능해지는것이 주문 수량 칼럼이다.

# 상세 설명
## 파일 저장 전략(이미지 저장전략)
```
random uuid + "_" + originalFileName = saveFileName 으로 저장
경로는 c:\temp\upload
불러올때에도 해당 경로로 불러옴.
```

## 게시글 수정
* 게시글을 수정시 두가지 조건이 생긴다.
* 첫째 : 기존 이미지를 수정하며 게시글 수정
* 둘째 : 기존 이미지를 수정하지 않고 게시글 수정
* multipartfile을 받을때 isEmpty()로 판별하여 비어있다면 둘째 조건을
* 비어있지 않고 파일이 채워져있다면 첫째 조건에 따라 수정한다.
* 즉 수정 로직(메소드)가 두개임.
* 또한 수정시 게시자인지 판별하고 수정한다.

## 리뷰(comment)
* /item/comment/{itemId} 에 진입하면 댓글리스트가 서버로 부터 전송되고,
* 최상단에 textarea(html)가 있고, 여기서 바로 댓글을 작성하는 구조로 한다.
* 아래는 간단한 예시이다.
```
<form url mehtod="post">
<textarea>어쩌구저쩌구....
</form>
<li>
commentList
</li>
```

## 주문
* 주문시 두 조건이 있다. 품절인가?, 주문 수량 - 재고 수량 이 0이하인가?
* 주문 수량 - 재고 수량 이 0 이하인경우 재고 수량이 -1이 되어버릴 수 있다.
* 따라서 이런경우 주문수량이 재고 수량보다 많다고 사용자에게 메세지를 날려준다.
* 품절은 item detail에서 클라언트로 품절되었다는것을 알려주고
* 클라이언트가 품절된 상품은 버튼을 품절로 바꾸고 차단하지만
* 이러한 차단을 넘고 url로 접근하는 유저 때문에 주문시 한 번더 품절 체크를 해준다.
* 따라서 item의 remaining 칼럼은 절대로 0미만으로 내려가서는 안된다.

## 주문취소
```
주문취소는 LocalDate를 활용해서 주문후 7일 안에 주문취소가 가능하도록 설정한다.
LocalDate로 저장된 생성날자에서 getDayOfYear()를 사용해서 365일중 몇일인지(예 : 260) 뽑아낸다.
그리고 그 수에 + 7을 더해준다. 7을 더하는것이 가능한 이유는 getDayOfYear() 값은 int 형이다.
그리고 LocalDate.now().getDayOfYear()로 현재 날짜를 출력하고 그 값이 생성날짜에 7을 더한 값과 비교한다.
주문취소가 가능하다면 1을 리턴하고, 아니라면 -1을 리턴하도록 했다.
컨트롤러단에서 입력받은 int 변수를 통해서 if문으로 1인지 아닌지 판별후 1이면 주문취소를 하고 아닐경우 
불가능 메세지를 내보낸다.
주문취소는 status를 CANCEL로 바꾸는 것이지, 절대 db에서 삭제가 아니다!!
```

## mypage
* 마이페이지에서는 권한에 따라 주문/등록상품 리스트를 볼 수 있다.
* 유저의 권한을 체크해서 SELLER일경우 /user/itemlist 를 볼 수 있고,
* 권한이 MEMBER일경우 /user/orderlist를 볼 수 있다.
* mypage에 서버에서 유저의 정보를 넘겨줄떄 auth도 같이 넘겨진다.
* 이떄 화면단에서는 auth를 판별해서 MEMBER일 경우 주문리스트 버튼을, SELLER일 경우 등록상품 버튼을 보여준다.
* 유저 정보를 보내준다.
* 주소는 언제나 등록(수정)이 가능하다.

## 연관관계 매핑
* Order & Item -> ManyToOne 단방향
* Order & Users ->  ManyToOne 단방향
* Item & Users -> ManyToOne 단방향
* Comment & Item  -> ManyToOne 단방향
* ToOne관계에서는 지연로딩이기에 n+1 문제해결을위해 jpql로 페치조인해서 성능최적화.

## 카테고리
```
카테고리는 
자기계발, 여행, 경제, 종교, 예술, 요리, 수험, 미정 
이 있다. 
카테고리는 navbar와 같은곳에 <li>의 형태로 넣어두고,
상품을 등록할때 select box를 사용하여 입력받는다. 
이 카테고리의 범위를 넘어서지 않는다. 
카테고리가 없을경우 null로 하는것이 아니라 미정을 선택하여 저장한다.
뷰에서는 미정일경우 카테고리를 숨기는 형식으로 진행한다.(템플릿엔진 등에도 if문이 있음, 판별가능)
postman에서는 한글이 깨진다. 인코딩 하는 것도 귀찮으니 테스트 할때에는 영어로 바꾸어서 테스트했다.
ex) : 여행 -> travel 등
```

## map 으로 객체 전송시 규약
* map으로 객체를 전송시 string으로 객체의 이름을 표시하는데,
* 현재 유저 : user
* 현재 보낼 객체(데이터) : body
* 나머지 이름은 키와 값의 이름이 동일.

# json body
## users
```
{
    "email" : "yc1234@gmail.com",
    "password" : "1234"
}
{
    "email" : "ms1234@gmail.com",
    "password" : "1234"
}
{
    "email" : "admin@libreria.com",
    "password" : "1234"
}
seoul - body, raw, text, /user/address, post
```
## item
```
form-data, application/json, requestpart
{
    "title" : "test1",
    "content" : "this is content",
    "author" : "chan",
    "remaining" : 3,
    "category" : "종교",
    "year" : "2022-10-12",
    "good" : 1
}
{
    "title" : "test2",
    "content" : "this is content2",
    "author" : "park",
    "remaining" : 3,
    "category" : "travel",
    "year" : "2022-10-14",
    "good" : 3
}
{
    "title" : "updated Title",
    "content" : "updated content",
    "author" : "park",
    "remaining" : 10,
    "category" : "travel",
    "year" : "2022-10-14"
}
```
## comment
```
{
    "content" : "this is comment"
}
{
    "content" : "updated comment"
}
```
## orders
```
{
    "orderCount" : "2"
}
```

# api
## users
```
/ - get
/user/signup - get/post
/user/login - get/post
/user/logout - post
/user/seller - get/post
/user/mypage - get
/user/address - get/post
/user/itemlist - get, auth가 SELLER인 user만 가능
/user/prohibition - get
/admin - get, auth가 ADMIN인 경우만 가능
```
## item
```
/item - get
/item/search - get, parameter : keyword
/item/category/{category} - get
/item/post - get/post
/item/{id} - get
/item/image/{saveFileName} - image url
/item/good/{id} - post
/item/edit/{id} - get/post
```
## comment
```
/item/comment/{itemId} - get
/item/comment/post/{itemId} - post, 댓글 홈에서 바로 댓글 작성함(textarea있음)
/item/comment/edit/{id} - get/post
/item/comment/delete/{id} - post
```
## orders
```
/item/orderlist/{itemId} - get, myPage에서 조회할 나의 주문리스트
/item/order/{itemId} - get/post
/item/cancel/{orderId} - get/post
```

## DB ERD diagram
![스크린샷(135)](https://user-images.githubusercontent.com/88976237/197521558-3d2b32da-ee75-4bda-a4c7-bde7c3d3ab18.png)