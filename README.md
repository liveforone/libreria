# libreria
> 온라인 서점

# 1. 사용 기술 스택
* Language : Java17
* DB : MySql
* ORM : Spring Data Jpa
* Spring Security
* LomBok
* Gradle
* Spring Boot 2.7.4

# 2. 설명
* 온라인 서점 사이트이다.
* rest-api서버이다.
* rest-api 서버이지만 클라이언트 단에서 어떻게 반응해야하는지에 대한 설계도 모두 적혀있다.
* 즉 화면단 까지 고려하여 설계하고 작성되었다.
* 결제로직은 분리했다. 즉 다루지 않았다.
* 결제로직 분리를 통해 결제로직에 치중하는 문제에서 벗어났다.
* 깃의 wiki에 사용기술들과 설명들에 대해 자세히 기록했다.  
* [나의 위키](https://github.com/liveforone/libreria/wiki)

# 3. 설계
* 유저는 ADMIN, MEMBER, SELLER 세 종류가 있다.
* 어드민페이지에서 모든 유저를 확인 가능하다.
* 유저이름은 모두 이메일 기반이다.
* 멤버는 서적을 주문, 리뷰 등록이 가능하다.
* 상품등록시 SELLER 권한이 필요하다.
* 회원은 등급이 있다. UserService의 getUser() 함수에서 등급을 체크한다.
* 브론즈, 실버(주문건수 15건 이상), 골드(30건), 플래티넘(60건), 다이아(120건)
* 등급은 my-page에서 나의 등급과 리뷰에서 게시자의 등급을 볼 수 있다.
* 도서의 사진은 1개만 첨부 가능하다. 도서는 많은 상품 이미지를 사실 필요로 하지않는다.(여타 사이트들도 마찬가지)
* 또한 사진이 없다면 도서의 등록은 불가능하다.(forbidden)
* 게시글 마다 리뷰게시판이 존재한다.
* 게시글의 게시자는 주문 리스트를 보는 것이가능하다. my-page와 비슷한 로직이다.
* 상품을 주문 할 때마다 상품 주문 횟수는 증가한다.
* 카테고리와 상품 홈 모두 좋아요(good) 순으로 페이징 정렬한다.
* 주문을 취소할때에는 반드시 주문한지 7일 이내에 취소해야한다.
* 품절된 상품은 body에 품절 메세지를 보내준다.
* 게시자는 상품의 수정이 가능하며, 품절시 재고 등록도 수정으로 한다.(게시자 판별은 서버와 화면단 모두에서함)
* 여타 다른 사이트가 그렇듯 상품은 품절됬음 품절됬지 게시글을 삭제하는것은 불가능하다.(어드민은 가능)

## DB ERD diagram
![스크린샷(140)](https://user-images.githubusercontent.com/88976237/201450990-04df79cd-1da2-4e63-9cee-badda3cf2039.png)

## json body 설계 및 예시
### users
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
{
    "oldPassword" : "1234",
    "newPassword" : "1111"
}
seoul - body, raw, text, /user/address, post
```
### item
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
### comment
```
{
    "content" : "this is comment"
}
{
    "content" : "updated comment"
}
```
### orders
```
{
    "orderCount" : "2"
}
```

## API 설계
### users
```
/ - get
/user/signup - get/post
/user/login - get/post
/user/logout - post
/user/seller - get/post
/user/my-page - get
/user/address - get/post
/user/item-list - get, auth가 SELLER인 user만 가능
/user/order-list - get, auth가 MEMBER인 user만 가능
/user/prohibition - get
/admin - get, auth가 ADMIN인 경우만 가능
```
### item
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
### comment
```
/item/comment/{itemId} - get
/item/comment/post/{itemId} - post, 댓글 홈에서 바로 댓글 작성함(textarea있음)
/item/comment/edit/{id} - get/post
/item/comment/delete/{id} - post
```
### orders
```
/item/order-list/{itemId} - get, myPage에서 조회할 나의 주문리스트
/item/order/{itemId} - get/post
/item/cancel/{orderId} - get/post
```

# 4. 스타일 가이드
* 유저를 제외한 모든 객체의 [널체크](https://github.com/liveforone/study/blob/main/GoodCode/%EA%B0%9D%EC%B2%B4%20null%EC%B2%B4%ED%81%AC%EC%99%80%20%EC%A4%91%EB%B3%B5%EC%B2%B4%ED%81%AC.md) + 중복 체크
* 함수와 긴 변수의 경우 [줄바꿈 가이드](https://github.com/liveforone/study/blob/main/GoodCode/%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%20%ED%96%A5%EC%83%81.md)를 지켜 작성한다.
* 매직넘버는 전부 [상수화](https://github.com/liveforone/study/blob/main/GoodCode/%EB%A7%A4%EC%A7%81%EB%84%98%EB%B2%84%20%EC%83%81%EC%88%98%EB%A1%9C%20%ED%95%B4%EA%B2%B0.md)해서 처리한다.
* 분기문은 반드시 [게이트웨이](https://github.com/liveforone/study/blob/main/GoodCode/%EB%8D%94%20%EC%A2%8B%EC%9D%80%20%EB%B6%84%EA%B8%B0%EB%AC%B8.md) 스타일로 한다.
* entity -> dto 변환 편의메소드는 리스트나 페이징이 아닌 경우 컨트롤러에서 사용한다.
* [HttpHeader 처리 함수](https://github.com/liveforone/study/blob/main/GoodCode/HttpHeaders%20%EC%83%9D%EC%84%B1%20%ED%95%A8%EC%88%98.md)
* 스프링 시큐리티에서 권한 체크 필요한것만 매핑하고 나머지(anyRequest)는 authenticated 로 설정해 코드를 줄이고 가독성 향상한다.
* Mapper 클래스에서 dtoBuilder 메소드의 경우 반드시 private 으로 설정해 접근을 제한한다.

# 5. 상세 설명
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

## my-page
* 마이페이지에서는 권한에 따라 주문/등록상품 리스트를 볼 수 있다.
* 유저의 권한을 체크해서 SELLER일경우 /user/itemlist 를 볼 수 있고,
* 권한이 MEMBER일경우 /user/orderlist를 볼 수 있다.
* my-page에 서버에서 유저의 정보를 넘겨줄떄 auth도 같이 넘겨진다.
* 이떄 화면단에서는 auth를 판별해서 MEMBER일 경우 주문리스트 버튼을, SELLER일 경우 등록상품 버튼을 보여준다.
* 유저 정보를 보내준다.
* 주소는 언제나 등록(수정)이 가능하다.

## 연관관계 매핑
* Order -> Item : ManyToOne 단방향
* Order -> Users :  ManyToOne 단방향
* Item -> Users : ManyToOne 단방향
* Comment -> Item : ManyToOne 단방향
* Bookmark -> Users, Item : ManyToOne 단방향
* ToOne관계에서는 지연로딩이기에 n+1 문제해결을위해 jpql로 페치조인해서 성능최적화.
* 연관관계에서는 모두 지연로딩으로 설정하여주었다.
* n+1 문제를 해결하기위해 페치조인으로 해결했다.
* 해당 내용은 위키를 참조. [페치조인 위키](https://github.com/liveforone/libreria/wiki/%ED%8E%98%EC%B9%98-%EC%A1%B0%EC%9D%B8)

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

## json 리턴시 null column 생략
* /user/my-page 가 대표적인 예시인데,
* address가 처음 가입하고 등록하지 않은경우 null일 수가 있다.
* 따라서 이때 null을 반환하고 싶지 않기때문에
* @JsonInclude(JsonInclude.Include.NON_NULL) 를 사용하여서 null인 column을 빼고 리턴했다.

## map 으로 객체 전송시 규약
* map으로 객체를 전송시 string으로 객체의 이름을 표시하는데,
* 현재 유저 : user
* 현재 보낼 객체(데이터) : body
* 나머지 이름은 키와 값의 이름이 동일.

# 6. 나의 고민
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

## 순환참조와 JsonBackReference & Dto
* 지속해서 순환 참조 문제가 발생하였다.
* 찾아보니 엔티티를 json으로 직렬화 하는 부분에서 발생한 문제였다.
* 해결 방법으로 내가 선택한것은 JsonBackReference 어노테이션이었는데 아무리생각해도,
* 엔티티 직접 리턴은 Users 테이블이 아니여도 좀 불안하고 좋지 않은 방법인것 같았다.
* 또한 순환 참조 문제를 완전히 해결하고 싶었다.
* 따라서 모든 엔티티를 dto로 바꾸어서 화면단에 전달하는 리팩토링 작업을 프로젝트 마지막에 진행하였다.
* 해당 내용은 밑의 위키에서 확인 가능하다.
* [Dto 위키](https://github.com/liveforone/libreria/wiki/Dto%EB%A1%9C-%EC%88%9C%ED%99%98%EC%B0%B8%EC%A1%B0-%ED%95%B4%EA%B2%B0)

## 복잡한 함수나 변수에서의 가독성 향상
* 긴 함수, 매개변수가 많은 함수, 긴 변수에서 가독성이 많이 떨어졌다.
* 또한 매개변수로 문자열이 들어가는 경우에도 가독성이 많이떨어졌다.
* 줄바꿈으로 가독성을 향상했고 이에대한 내용은 아래 링크에서 확인 가능하다.
* [줄바꿈으로 가독성 향상](https://github.com/liveforone/study/blob/main/GoodCode/%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%20%ED%96%A5%EC%83%81.md)

# 7. 새롭게 적용한 점
* 생성이나 수정시 getId()로 id입력받아서 리다이렉트 간편히 처리했다.
* 수정과 삭제 모두 서버에서도 권한 체크.
* dto로 리턴하여 순환참조 및 민감한 내용 뷰에 전달 막음.
* null인 컬럼 빼고 리턴하는 어노테이션 추가함.
* 객체의 널체크 추가됨.
* 회원 중복 체크 추가됨.
* 회원 탈퇴, id, pw 변경 추가됨.
* 북마크 기능 추가됨
* 분기문 버블스타일에서 gate way 스타일로 변경
* 신청 서비스 중복체크(쿼리 and절 이용)
* 신청서비스 같은 and query 네이밍 수정 -> findOne엔티티이름
* dto -> entity 메소드 서비스로직으로 이동 후 서비스 로직에서 처리
* 반복되는 entity -> dto builder 를 함수화 해서 불필요한 반복을 줄임.
* 뷰전달이 아닌 검증을 위해 값을 조회하는 것은 엔티티를 직접 리턴하여 성능 향상시킴.
* 긴함수와 긴 변수는 줄바꿈하여 가독성을 올렸다.
* orderlist -> order-list로 수정함. 모든 api 중 다음과 같은 형식이 있다면 전면 수정함.(오류발생시 api확인 유의)
* 상수로 매직넘버 대체하여 가독성 향상
* 널체크는 util 클래스를 만들고 커스텀 함수인 isNull()을 이용해 처리하는 것으로 전면 수정함.
* [널체크 커스텀 함수](https://github.com/liveforone/study/blob/main/GoodCode/%EA%B0%9D%EC%B2%B4%20null%EC%B2%B4%ED%81%AC%EC%99%80%20%EC%A4%91%EB%B3%B5%EC%B2%B4%ED%81%AC.md)
* HttpHeaders 축약 함수로 가독성 및 중복코드 제거함.
* 시큐리티에 권한 매핑 필요한 것 아닌 나머지(anyRequest)에 authenticated로 설정해 가독성 향상함.
* 문서(readme)에 스타일 가이드 추가함.
* 향상된 for-each 문 람다 for-each 문으로 변경하여 가독성 향상
* [dto 직접조회](https://github.com/liveforone/study/blob/main/GoodCode/dto%20%EC%A7%81%EC%A0%91%EC%A1%B0%ED%9A%8C%EB%A5%BC%20%EC%95%A0%EC%9A%A9%ED%95%98%EC%9E%90.md)로 가독성과 성능 향상함.
* mapper 클래스와 XxUtils 클래스를 만들어서 dto <-> entity 로직은 mapper로, transaction 이 걸리지 않는 로직은 XxUtils로 모듈화 하여 서비스로직의 가독성향상과 모듈화로 객체지향을 더욱 지킴.