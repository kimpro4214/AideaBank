# spring-gift-order

## 0단계 - 기본 코드 준비

## 1단계 - 구현할 기능 목록

- [x] 카카오 인가 코드 요청 URL 생성
- [x] 인가 코드로 리디렉션 받을 URI 설정 및 컨트롤러 생성
- [x] 인가 코드로 액세스 토큰 요청 (POST /oauth/token)
- [x] 액세스 토큰 응답 파싱 (KakaoTokenResponse)
- [x] 카카오 설정 정보 외부 설정 파일로 분리 (client-id, redirect-uri 등)
- [x] 인가 코드 및 토큰 요청 예외 처리 (403, 400 등)
- [ ] (선택) 로그인 성공 시 사용자에게 액세스 토큰 응답

###  카카오 인가 코드 요청 URL
https://kauth.kakao.com/oauth/authorize?scope=talk_message&response_type=code&redirect_uri=http://localhost:8080/auth/kakao/callback&client_id=YOUR_CLIENT_ID

## 2단계 - 구현할 기능 목록
- [x] 주문 요청을 위한 REST API 엔드포인트 생성 (POST /api/orders)
- [x] 요청 본문에 포함된 optionId, quantity, message 파라미터 처리
- [x] 주문 생성 시 재고 수량 차감
- [x] 위시리스트에서 해당 상품 옵션 제거 
- [x] 사용자 액세스 토큰으로 카카오 메시지 API 호출
- [x] 메시지 템플릿 구성 및 전송
- [x] 메시지 전송 대상은 "나에게 보내기"로 제한
- [x] 주문 생성 성공 시 201 응답 반환
- [x] 응답 본문에 주문 정보 포함 (id, optionId, quantity, orderDateTime, message)


