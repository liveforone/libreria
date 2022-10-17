# libreria
> 중고도서 거래 사이트

## 설명
* 중고도서 거래 사이트이다.
* rest-api서버이다.
* 결제로직은 분리했다. 즉 다루지 않았다.
* 결제로직 분리를 통해 결제로직에 치중하는 문제에서 벗어났다.
* 깃의 wiki에 사용기술들과 설명들에 대해 자세히 기록했다.
[나의 위키](https://github.com/liveforone/libreria/wiki)

## 설계
* 유저는 ADMIN, MEMBER 두 종류가 있다.
* 어드민페이지에서 모든 유저를 확인 가능하다.
* 멤버는 중고서적을 등록, 주문이 가능하다.(일반 서점보다 상품등록에 자유도가 높음)
* 일반 서점처럼 상품등록에 SELLER 권한을 넣지 않았다.
* 회원은 등급이 있다. 유저서비스의 checkClass() 함수가 이 등급을 표시한다.
* 브론즈, 실버(주문건수 15건 이상), 골드(30건), 플래티넘(60건), 다이아(120건)
* 등급은 mypage에서 나의 등급과 상품 detail 에서 게시자의 등급을 볼 수 있다.
* 도서의 사진은 1개만 첨부 가능하다. 도서는 많은 상품 이미지를 사실 필요로 하지않는다.(여타 사이트들도 마찬가지)
* 또한 사진이 없다면 도서의 등록은 불가능하다.(forbidden)
* 게시글 마다 리뷰게시판이 존재한다.
* 상품을 주문 할 때마다 상품 주문 횟수는 증가하며 이것을 기준으로 페이징이된다.
* 즉 페이징할때 정렬되는 순서는 주문 횟수이다.
* 주문을 취소할때에는 반드시 주문한지 7일 이내에 취소해야한다.
* 품절된 상품은 body에 품절 메세지를 보내준다.
* 여타 다른 사이트가 그렇듯 상품은 품절됬음 품절됬지 게시글을 삭제하는것은 불가능하다.(어드민은 가능)

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
* multifile을 받을때 isEmpty()로 판별하여 비어있다면 둘째 조건을
* 비어있지 않고 파일이 채워져있다면 첫째 조건에 따라 수정한다.
* 즉 수정 로직(메소드)가 두개임.

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
* mypage에서는 나의 회원 등급과 나의 주소를 내보낸다.
* 유저 주소는 주소가 있을경우 해당 주소를 보내준다.
* 주소가 없을경우 mypage에서 주소를 보내주지않는다.
* 주소는 언제나 등록(수정)이 가능하다.
* orderList를 통해서 나의 주문 리스트를 확인 할 수 있다.

배운점과 사용 기술과 알고리즘에대한 설명을 자세히
왜 이런 패턴을 쓰고 왜 이런걸 적용했는지 세세히 작상
그것이 빌더패턴일지라도 말이다
editora 보단 libro를 더욱 고도화 한다고 생각하면된다

예외처리 등을 신경쓴 시스템
테스트코드
게시글 수정시 파일은 libro의 방식말고 내가 원래 쓰는 방식으로 판별식으로 만들기
회원등급은 mypage에 접근하면 user에 count 확인해서 등급매기고 body에 쏴주기
yml 설정 내용 확인 후 도큐먼테이션
https://jaimemin.tistory.com/1516
https://velog.io/@fada2020/yml-%EC%98%B5%EC%85%98
seller권한 없애버리기

mypage와 detail에서 map으로 유저의 등급 내보내기
주문시 itemTitle이 아니라 itemId저장하는 것으로 함.

admin id = admin@libreria.com