# libreria
> 온라인 서점, 프로젝트 생성 시점(2022.10.17)부터 계속 릴리즈(리팩터링)되고 있습니다.

# 1. 사용 기술 스택
* Spring Boot 2.7.4 -> 3.0.0(마이그레이션)
* Language : Java17
* DB : MySql
* ORM : Spring Data Jpa
* Spring Security
* LomBok
* Gradle
* jwt(api, impl, jackson)
* Junit5

# 2. 설명
* 온라인 서점 사이트이다.
* rest-api서버이다.
* rest-api 서버이지만 클라이언트 단에서 어떻게 반응해야하는지에 대한 설계도 모두 적혀있다.
* 즉 화면단 까지 고려하여 설계하고 작성되었다.
* 결제로직은 분리했다. 즉 다루지 않았다.
* 결제로직 분리를 통해 결제로직에 치중하는 문제에서 벗어났다.
* 깃의 wiki에 사용기술들과 설명들에 대해 자세히 기록했다.  
* [나의 위키](https://github.com/liveforone/libreria/wiki)
* 부족한 부분들을 점차 채워나가는 프로젝트이다.

# 3. 설계
* 유저는 ADMIN, MEMBER, SELLER 세 종류가 있다.
* 어드민페이지에서 모든 유저를 확인 가능하다.
* 유저이름은 모두 이메일 기반이다.
* 멤버는 서적을 주문, 리뷰 등록이 가능하다.
* 상품등록시 SELLER 권한이 필요하다.
* 회원은 등급이 있다.
* 브론즈, 실버(주문건수 15건 이상), 골드(30건), 플래티넘(60건), 다이아(120건)
* 등급은 my-page에서 나의 등급과 리뷰에서 게시자의 등급을 볼 수 있다.
* 도서의 사진은 1개만 첨부 가능하다. 도서는 많은 상품 이미지를 사실 필요로 하지않는다.(여타 사이트들도 마찬가지)
* 또한 사진이 없다면 도서의 등록은 불가능하다.
* 게시글 마다 리뷰게시판이 존재한다.
* 리뷰에 욕설을 작성할경우 필터링 봇이 욕설을 감지하고 리뷰는 작성되지않는다.
* 이때 회원 등급은 -5점 된다.
* 게시글의 게시자는 주문 리스트를 보는 것이가능하다. my-page와 비슷한 로직이다.
* 상품을 주문 할 때마다 상품 주문 횟수는 증가한다.
* 카테고리와 상품 홈 모두 좋아요(good) 순으로 페이징 정렬한다.
* 주문을 취소할때에는 반드시 주문한지 7일 이내에 취소해야한다.
* 품절된 상품은 body에 품절 메세지를 보내준다.
* 게시자는 상품의 수정이 가능하며, 품절시 재고 등록도 수정으로 한다.(게시자 판별은 서버와 화면단 모두에서함)
* 여타 다른 사이트가 그렇듯 상품은 품절됬음 품절됬지 게시글을 삭제하는것은 불가능하다.(어드민은 가능)
* 테스트 코드는 기능에 대해서 작성했다. 예를 들어 주문을 취소할때 7일 안에 취소가 가능하다면 이런 날짜를 체크하는 함수를 테스트한다고 보면된다.

## API 설계
### users
```
[GET] / : 홈
[GET/POST] /user/signup : Authorization 헤더 설정하지 마라!! 해당 헤더가 필요없는 api이며 설정시 에러 발생한다. 
[GET/POST] /user/login : Authorization 헤더 설정하지 마라!! 해당 헤더가 필요없는 api이며 설정시 에러 발생한다.
[GET] /user/logout : Authorization 헤더 설정하지 마라!! 해당 헤더가 필요없는 api이며 설정시 에러 발생한다.
[GET/POST] /user/seller : 판매자로 권한 업데이트
[GET] /user/my-page : 마이 페이지
[GET/POST] /user/regi-address : 주소 등록 
[PUT] /user/change-email : 이메일 변경, UserChangeEmailRequest 형식 필요
[PUT] /user/change-password : 비밀번호 변경, UserChangePasswordRequest 형식 필요
[GET] /user/item-list : auth가 SELLER인 user만 가능, 내가 등록한 상품 리스트
[GET] /user/order-list : auth가 MEMBER인 user만 가능, 내가 주문한 상품 리스트
[GET] /user/prohibition : 접근 금지, status : 403
[GET] /admin : auth가 ADMIN인 경우만 가능, 어드민 페이지
[DELETE] /user/withdraw : 계정 탈퇴
```
### item
```
[GET] /item : 전체 상품 페이지, 좋아요를 기준으로 정렬됨
[GET] /item/search : 상품 검색, 좋아욜르 기준으로 정렬됨, 제목으로 검색, parameter : keyword
[GET] /item/category/{category} : 카테고리로 정렬
[GET/POST] /item/post : 상품 등록, auth : SELLER
[GET] /item/{id} : 상품 detail
[PUT] /item/good/{id} : 상품 좋아요
[GET/PUT] /item/edit/{id} : 상품 수정
```
### comment
```
[GET] /comment/{itemId} : 댓글 리스트
[POST] /comment/post/{itemId} : 댓글 작성 댓글 리스트에서 바로 댓글 작성함(textarea있음)
[GET/PUT] /comment/edit/{id} : 댓글 수정, 작성자만 접근 가능
[DELETE] /comment/delete/{id} : 댓글 삭제, 작성자만 접근 가능
```
### orders
```
[GET] /item/order-list/{itemId} : myPage에서 조회할 나의 주문리스트
[GET/POST] /item/order/{itemId} : 상품 주문
[GET] /item/order/{itemId} : 주문 상세
[GET/DELETE] /item/cancel/{orderId} : 상품 주문 취소, 7일 안에 가능f
```
### file
```
[GET] /file/{saveFileName} : image url
```
### Bookmark
```
[GET] /my-bookmark : 나의 북마크 보기
[POST] /bookmark/post/{itemId} : 북마킹 
[DELETE] /bookmark/cancel/{itemId} : 북마크 취소
```

## json body 설계 및 예시
### users
```
[일반 유저1]
{
    "email" : "yc1234@gmail.com",
    "password" : "1234"
}
[일반 유저2]
{
    "email" : "ms1234@gmail.com",
    "password" : "1234"
}
[어드민]
{
    "email" : "admin@libreria.com",
    "password" : "1234"
}
[비밀번호 변경]
{
    "oldPassword" : "1234",
    "newPassword" : "1111"
}
[주소 등록/변경]
body(raw, text) : seoul
```
### item
```
[도서 등록1]
form-data 에 application/json 형태로 아래 json 삽입
json 다음에 uploadFile 이라는 이름으로 파일 등록
{
    "title" : "test1",
    "content" : "this is content",
    "author" : "chan",
    "remaining" : 3,
    "category" : "종교",
    "publishedYear" : "2022-10-12",
    "good" : 1
}
[도서 등록2] - 형식은 위와 동일
{
    "title" : "test2",
    "content" : "this is content2",
    "author" : "park",
    "remaining" : 3,
    "category" : "travel",
    "publishedYear" : "2022-10-14",
    "good" : 3
}
[도서 등록3] - 형식은 위와 동일
{
    "title" : "updated Title",
    "content" : "updated content",
    "author" : "park",
    "remaining" : 10,
    "category" : "travel",
    "publishedYear" : "2022-10-14"
}
```
### comment
```
[댓글 등록]
{
    "content" : "this is comment"
}
[댓글 수정]
{
    "content" : "updated comment"
}
```
### orders
```
[주문]
{
    "orderCount" : "2"
}
```

# 4. 데이터 베이스 설계
## 간단 설명
* 쿼리 생성이 귀찮을 경우 ddl-auto를 create로 설정하고 create, alter 쿼리를 복사하여 나만의 제약조건을 넣는다.
* ddl-auto를 create-drop으로 설정하여 모든 테이블을 다 날려준다.
* 위에서 만들어놓은 쿼리를 mysql workbench에서 실행시켜 테이블을 생성한다.
* ddl-auto를 none으로 놓고 사용한다.
* 이번 프로젝트에서는 쿼리를 직접 사용하는 것을 목표로 하였기에 대단한 성능을 생각하는 제약조건은 딱히 없다.
## 쿼리
```
create table users (
    id bigint not null auto_increment,
    email varchar(255) not null,
    password varchar(255) not null,
    auth varchar(255),
    address varchar(255),
    count integer default 0,
    primary key (id)
);

create table item (
    id bigint not null auto_increment,
    title varchar(255) not null,
    content TEXT,
    author varchar(255),
    category varchar(255),
    published_year varchar(255),
    good integer default 0,
    remaining integer not null,
    users_id bigint,
    primary key (id)
);

create table orders (
    id bigint not null auto_increment,
    status varchar(255),
    created_date date,
    order_count integer default 0,
    item_id bigint,
    users_id bigint,
    primary key (id)
);

create table comment (
    id bigint not null auto_increment,
    content TEXT not null,
    writer varchar(255),
    created_date datetime(6),
    item_id bigint,
    primary key (id)
);

create table bookmark (
    id bigint not null auto_increment,
    item_id bigint,
    users_id bigint,
    primary key (id)
);

create table upload_file (
       id bigint not null auto_increment,
        save_file_name varchar(255),
        item_id bigint,
        primary key (id)
);

alter table item add foreign key (users_id) references users (id);
alter table orders add foreign key (item_id) references item (id);
alter table orders add foreign key (users_id) references users (id);
alter table comment add foreign key (item_id) references item (id);
alter table bookmark add foreign key (item_id) references item (id);
alter table bookmark add foreign key (users_id) references users (id);
alter table upload_file add foreign key (item_id) references item (id);
```
## DB ER Diagram
![스크린샷(153)](https://user-images.githubusercontent.com/88976237/208883315-0f0c9f25-7471-4d88-a566-2cbe6b53a130.png)


# 5. 스타일 가이드
* 함수와 긴 변수의 경우 [줄바꿈 가이드](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/b.%20%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%EC%9D%84%20%ED%96%A5%EC%83%81%ED%95%98%EC%9E%90.md)를 지켜 작성하라.
* 유저를 제외한 모든 객체의 [널체크](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/c.%20%EA%B0%9D%EC%B2%B4%EC%9D%98%20Null%EA%B3%BC%20%EC%A4%91%EB%B3%B5%EC%9D%84%20%EC%B2%B4%ED%81%AC%ED%95%98%EB%9D%BC.md) + 중복 체크를 꼭 하라.
* 분기문은 반드시 [게이트웨이](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/d.%20%EB%B6%84%EA%B8%B0%EB%AC%B8%EC%9D%80%20gate-way%20%EC%8A%A4%ED%83%80%EC%9D%BC%EB%A1%9C%20%ED%95%98%EB%9D%BC.md) 스타일로 하라.
* [Mapper 클래스](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/e.%20Mapper%20%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A5%BC%20%EB%A7%8C%EB%93%A4%EC%96%B4%20Entity%EC%99%80%20Dto%EB%A5%BC%20%EC%83%81%ED%98%B8%20%EB%B3%80%ED%99%98%ED%95%98%EB%9D%BC.md)를 만들어 entity와 dto를 상호 변환하라.
* 단순 for-each문은 [람다](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/f.%20%EB%8B%A8%EC%88%9C%20for-each%EB%AC%B8%EC%9D%84%20%EB%9E%8C%EB%8B%A4%EB%A1%9C%20%EB%B0%94%EA%BE%B8%EC%9E%90.md)로 바꿔라.
* 매직넘버는 전부 [enum](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/h.%20%EB%A7%A4%EC%A7%81%EB%84%98%EB%B2%84%EB%A5%BC%20enum%EC%9C%BC%EB%A1%9C%20%ED%95%B4%EA%B2%B0%ED%95%98%EB%9D%BC.md)으로 처리하라.
* 스프링 시큐리티에서 권한 체크 필요한것만 매핑하고 나머지(anyRequest)는 authenticated 로 설정해 코드를 줄이고 가독성 향상하라.
* [Utils 클래스](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/i.%20Util%20%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A5%BC%20%EB%A7%8C%EB%93%A4%EC%96%B4%20%ED%8E%B8%EC%9D%98%EC%84%B1%EC%9D%84%20%EB%86%92%EC%97%AC%EB%9D%BC.md)를 적극 활용하고, 서비스로직에서 트랜잭션이 걸리지 않는 로직은 Utils 클래스에 담아서 모듈화하라.
* [네이밍은 직관적이게 하라](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/j.%20%EB%84%A4%EC%9D%B4%EB%B0%8D%EC%9D%80%20%EC%A7%81%EA%B4%80%EC%A0%81%EC%9D%B4%EA%B2%8C%20%ED%95%98%EB%9D%BC.md)
* 주석은 c언어 스타일 주석으로 선언하라.
* [함수 규칙](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/k.%20%ED%95%A8%EC%88%98%20%EA%B7%9C%EC%B9%99.md)을 지켜라.
* [좋은 테스트 코드 작성법](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/l.%20%EC%A2%8B%EC%9D%80%20%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EC%9E%91%EC%84%B1%ED%95%98%EA%B8%B0.md)
* [양방향 연관관계를 지양하라](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/m.%20%EC%96%91%EB%B0%A9%ED%96%A5%20%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84%EB%A5%BC%20%EC%A7%80%EC%96%91%ED%95%98%EB%9D%BC.md)

# 6. 상세 설명
## Authorization 헤더 설정하면 안되는 api
* 회원가입, 로그인, 로그아웃을 할때에는 postman 뿐만 아니라 프론트엔드에서도 authorization 헤더를 설정하면 안된다.
* 왜냐하면 해당 api들은 authorization 헤더가 필요없는 api들이기 때문이다.
* 따라서 이러한 api들에 authorization 헤더를 걸게되면 SignatureException이 발생하게된다.
* 따라서 위의 api들은 꼭! authorization 헤더를 넣지말자.

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
* 주문취소는 LocalDate를 활용해서 주문후 7일 안에 주문취소가 가능하도록 설정한다.
* LocalDate로 저장된 생성날자에서 getDayOfYear()를 사용해서 365일중 몇일인지(예 : 260) int형으로 뽑아낸다.
* getDayOfYear() 에 7을 더한 날짜보다 nowDate()가  크면 주문취소가 불가능하다.
* 문제는 한해가 넘어가면 getDayOfYear()는 다시 1로 초기화 된다.
* 즉 1월 1일은 2022년도 2023년도, 2024년도 모두 1이란 수를 갖는다.
* 따라서 12월 25일부터 31일 까지의 날짜들은 모두 7을 더하면 365라는 숫자를 넘어가버려서 비교가 불가능해진다.
* 이들은 모두 swtich문을 사용해서 12월 25일은 마감 날짜가 1,
* 31일은 7로 하여 하나씩 값을 넣어주었다.
* 주문취소는 status를 CANCEL로 바꾸는 것이지, 절대 db에서 삭제가 아니다!!
```
LocalDate createdDate = orders.getCreatedDate();
int orderDate = createdDate.getDayOfYear();

int nowYear = LocalDate.now().getYear();
int nowDate = LocalDate.now().getDayOfYear();

int cancelLimitDate = switch (orderDate) {
    case 359 -> LocalDate.of(nowYear, 1, 1).getDayOfYear();
    case 360 -> LocalDate.of(nowYear, 1, 2).getDayOfYear();
    case 361 -> LocalDate.of(nowYear, 1, 3).getDayOfYear();
    case 362 -> LocalDate.of(nowYear, 1, 4).getDayOfYear();
    case 363 -> LocalDate.of(nowYear, 1, 5).getDayOfYear();
    case 364 -> LocalDate.of(nowYear, 1, 6).getDayOfYear();
    case 365 -> LocalDate.of(nowYear, 1, 7).getDayOfYear();
    default -> orderDate + 7;
};

return nowDate > cancelLimitDate;
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

## 권한 업데이트
* 권한 업데이트 시 해당 토큰에 업데이트된 권한이 반영이 안된다.
* 따라서 로그아웃으로 리다이렉트 하였다.
* 다시 로그인하면 권한이 업데이트된다.
* 토큰은 만료될때까지 유효하지만 재로그인을 한 이유는 권한은 토큰과 관계없기 때문이다.
* CustomUserDetailService에서 createUserDetails 메소드가 유저를 db에서 끌어와서
* 권한, 이메일, 비밀번호등을 set하는데 이 메소드를 호출하는 메소드는 loadUserByUsername() 메소드이다.
* loadUserByUsername() 메소드는 로그인을 할때 호출되므로 권한을 업데이트 한 경우에는 반드시 재로그인을 해야 위의 로직을 돌고 제대로 권한이 매핑된다.
* 다시 말해서 로그인을 할때마다 token이 생성되는데, 권한을 업데이트하고 리매핑 하려면 다시 재 로그인해서 토큰을 재발급 받으면 된다는 것이다.

## Postman으로 로그아웃
* 로그아웃시 Http Method는 get이다.
* 토큰넣고 get으로 로그아웃해야 정상 작동한다.

## 회원가입
* 회원가입은 회원가입과 동시에 로그인 처리 되도록했다.
* 회원가입시 postman에는 http method로 post가 설정되어있고
* request dto도 회원가입을 위해 이미 작성되어져 있다.
* 두가지 조건을 활용해서 회원가입 후에 리다이렉트 시에 현재 가지고 있는 토큰만 가져와서 헤더에 추가하고 리다이렉트 하는 방법으로 진행하였다.

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

## 리다이렉트
* jwt에서 리다이렉트는 조금 복잡하다.
* 현재 가지고 있는 토큰을 꺼내서 넣어주어야하기 때문이다.
* CommonUtils 클래스에 makeRedirect() 함수를 만들어서 이 함수를 호출하여 리다이렉트 할 수 있게 했다.
* 해당 함수는 http method를 get으로 설정하고, 현재 토큰값을 꺼내어 집어놓고, 원하는 url에 해당 값들을 보내는 함수이다.
* 코드는 아래와 같다.
```
public static ResponseEntity<String> makeRedirect(String inputUrl, HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();

        String url = "http://localhost:8080" + inputUrl;
        String token = JwtAuthenticationFilter.resolveToken(request);
        httpHeaders.setBearerAuth(token);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class
        );
}
```

## 스프링부트 마이그레이션
* 스프링부트를 2.7.4에서 3.0.0으로 마이그레이션 하였다.
* 마이그레이션의 가장 큰 이유는 3.0.0에서의 스프링 부트 성능 변화와 스프링의 자카르타에 대한 전폭적인 지원,
* 마지막으로 스프링 시큐리티도 많은 부분 변화되었기 때문이다.
* 큰 변화가 있을때 마이그레이션 하지 않으면 나중에 마이그레이션 작업을 할 때 힘들어 질것같아 마이그레이션을 결정했다.
* 마이그레이션 진행은 아래와 같다.
```
gradle에
runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator' 
를 추가한다.

plugin { } 에서 자바의 버전을 원하는 버전으로 변경하는데
2.7.4에서 3.0.0 으로 넘어갈때 plugin에는 약간의 변화가 있다.

id 'io.spring.dependency-management' version '1.1.0' 
요 친구가 추가되었다.

따라서 아래와 같이 변경하면된다.

plugins {
	id 'java'
	id 'org.springframework.boot' version '3.0.0'
	id 'io.spring.dependency-management' version '1.1.0'
}

gradle을 동기화 해주고 거의 모든 로직은 동일하나 엔티티와 스프링 시큐리티만 변경을 해주었다.
엔티티는 import에서 persistance가 jakarta.persistence로 변경되었기 때문에 
alt + enter를 눌러서 적절한 값들을 import 해주면된다.
큰 이슈는 없었다. 딱히 큰 에러도 발생하지 않았다.
모든 마이그레이션 작업이 끝나면 runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator'는 삭제하여 
다시 gradle을 동기화 해주면된다.
```

# 7. 나의 고민
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
* [줄바꿈으로 가독성 향상](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/b.%20%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%EC%9D%84%20%ED%96%A5%EC%83%81%ED%95%98%EC%9E%90.md)

## 언제 리다이렉트 하고, 언제 포워드를 쓸까? 또 포워드인 경우에 어떻게 서버는 응답해줄까?
* 리다이렉트와 포워드를 구분할 필요가있다고 느꼈다.
* 시스템(세션 혹은 db)에 변화가 생기는 요청(로그인, 회원가입, 글쓰기, 필드 업데이트)의 경우에는 리다이렉트가 바람직하고, 
* 시스템에 변화가 생기지 않는 요청인 단순조회(검색) 등은 포워드를 사용하는것이 바람직하다. 
* 포워드를 쓸경우에는 rest-api서버에서 ResponseEntity.ok("메세지") 로 메세지만 클라이언트에 넘겨주도록 한다.
* 하지만 프로젝트에서 대부분의 경우에는 시스템에 변화가 생기는 요청이 대부분이다. 따라서 거의 리다이렉트가 쓰였다.

## 유저의 널체크
* 유저는 널체크를 할 필요가 없다.
* 그 이유는 로그인, 즉 인증을 받지 않은상태(토큰이 없는 상태)이면 권한잆다고 바로 403코드를 리턴하기 때문이다.
* 유저의 널체크를 하는 것은 논리적인 오류이고, 쓸데없는 자원낭비이다.
* 이미 시큐리티에서 체크한 내용을 또 하지 말자.

# 8. 릴리즈 노트
* [추가] 생성이나 수정시 getId()로 받아 남겨서 리다이렉트 간편히 처리
* [추가] dto로 리턴하여 순환참조 및 민감한 내용 뷰에 전달 막음
* [추가] 객체의 널체크 추가
* [추가] 회원 중복 체크 추가
* [추가] 회원 탈퇴, id, pw 변경 추가
* [추가] 북마크 기능 추가
* [리팩토링] 분기문 버블스타일에서 gate way 스타일로 변경
* [추가] 신청 서비스 중복체크(쿼리 and절 이용)
* [리팩토링] dto -> entity 메소드 서비스로직으로 이동 후 서비스 로직에서 처리
* [리팩토링] 반복되는 entity -> dto builder 를 함수화
* [추가, 리팩토링] 뷰 전달이 목적이 아닌 값을 사용하기 위해 조회쿼리를 날리는 것은 엔티티를 직접 리턴하여 성능 향상
* [리팩토링] 긴 함수와 긴 변수는 줄바꿈하여 가독성향상
* [리팩토링] 카멜케이스로된 api 전부 슬래시를 사용해 분류하여 rest-api 형식 지킴
* [추가, 리팩토링] 매직넘버를 상수로 대체하여 가독성 향상
* [리팩토링] 널체크는 util 클래스를 만들고 커스텀 함수인 isNull()을 이용해 처리하는 것으로 전면 수정
* [추가] 널체크 커스텀 함수 추가
* [추가, 리팩토링] HttpHeaders 축약 함수로 가독성 및 중복코드 제거
* [리팩토링] 시큐리티에 권한 매핑 필요한 것 아닌 나머지(anyRequest)에 authenticated로 설정
* [추가] 문서(readme)에 스타일 가이드 추가, [링크](https://github.com/liveforone/study/tree/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D)
* [리팩토링] 향상된 for-each 문을 람다로 변경
* [추가, 리팩토링] dto 직접조회로 가독성과 성능 향상
* [추가, 리팩토링] mapper 클래스와 Utils 클래스를 만들어서 dto <-> entity 로직은 mapper로, transaction 이 걸리지 않는 로직은 XxUtils로 모듈화
* [리팩토링] 주석은 c언어 스타일의 주석으로 변경
* [리팩토링] 상수는 enum으로 변경하여 타입 안전성, 인스턴스 단일화
* [리팩토링] 스프링부트의 버전을 2.7.4에서 3.0.0 버전으로 마이그레이션
* [추가] Jwt를 도입하여 기존 세션기반 회원관리에서 jwt 토큰 기반의 회원관리로 변경
* [리팩토링] ddl-auto: create 로 쿼리 자동생성하는 방식에서 직접 쿼리를 생성하는 방식으로으로 변경(실무에 가깝게 변경)
* [추가, 리팩토링] 모든 파일과 변수, 함수의 네이밍에 대한 스타일 가이드 제작 및 변경
* [추가, 리팩토링] 함수 스타일 가이드 제작 및 변경
* [리팩토링] Item에 파일을 저장하는 구조에서 파일(UploadFile)과 Item 분리(단일 책임 지게됨)
* [리팩토링] 여러번 사용되는 인라인 함수 변수화
* [추가] 필터링 봇을 만들어 댓글 욕설 필터링로직 추가
* [추가] 양방향 연관관계 지양하는 스타일 가이드 추가