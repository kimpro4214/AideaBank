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




