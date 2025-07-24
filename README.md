# spring-gift-enhancement

## 0단계 - 기본 코드 준비

## 1단계 - 구현할 기능 목록

- [x] Member 엔티티를 작성하고 JPA 매핑
- [x] Product 엔티티를 작성하고 JPA 매핑
- [x] Wish 엔티티를 작성하고 JPA 매핑 (Member와 Product와의 연관관계 포함)
- [x] MemberRepository 작성
- [x] ProductRepository 작성
- [x] WishRepository 작성
- [x] Member 엔티티 저장/조회 테스트 작성
- [x] Product 엔티티 저장/조회 테스트 작성
- [x] Wish 엔티티 저장/조회 테스트 작성

## 2단계 - 구현할 기능 목록
- [x] 상품 조회 페이지네이션
- [x] 위시리스트 조회 페이지네이션

## 3단계 - 구현할 기능 목록

- [x] 상품에 옵션 추가 기능
    - 옵션 이름: 공백 포함 50자 제한
    - 옵션 이름 허용 특수 문자: ( ), [ ], +, -, &, /, _
    - 옵션 수량: 1개 이상 1억 미만
    - 동일 상품 내 옵션 이름 중복 불가
- [x] 상품 조회 시 옵션 리스트 조회 기능 (API: GET /api/products/{productId}/options)
- [x] 옵션 수량 차감 기능 (서비스 또는 엔티티 내에서만 구현, API 없음)
- [x] (선택) 관리자 화면에서 옵션 추가 기능



