📌 1. 유저 식별 방식 (user_id 쿠키)

✔ 첫 방문 시 백엔드가 uuid를 생성

프론트가 /api/init 호출하면, 서버는 user_id 라는 쿠키를 내려줍니다.

```
Set-Cookie: user_id=1b3f-9dd3-xxxx; Path=/; Max-Age=2592000
```

---
📌 2. 설문 상태 저장 방식

서버는 user_id를 기준으로 내부 CSV 파일에 다음과 같이 저장합니다:
```
user_id,gds,memory,attention,language,draw
1b3f-9dd3,true,false,true,false,false
88a2-1c91,false,false,false,false,false
```
각 칼럼은 설문별 완료 여부를 의미합니다.

프론트는 이 CSV를 몰라도 되고, 백엔드에 상태만 묻습니다.
---
📌 3. 프론트에서 필요한 API (요약)
✔ [1] 사용자 초기화 (첫 방문 시 반드시 1번 호출)
GET /api/init

➡ user_id 쿠키 생성
➡ 백엔드에서 CSV에 사용자 row 생성

프론트는 아무 작업할 필요 없음
백엔드가 Set-Cookie로 쿠키를 내려주면 자동 저장됨.
---
✔ [2] 설문 시작 처리

POST /api/survey/start?type={surveyType}

```
POST /api/survey/start?type=gds
```

서버가 해당 사용자(user_id)의
gds=false로 저장 → “시작함” 표시 의미.

프론트는 응답만 확인하면 됩니다.
---
✔ [3] 설문 완료 처리
POST /api/survey/complete?type={surveyType}
```
POST /api/survey/complete?type=gds
```

서버가 CSV에 gds=true로 저장.

프론트는 완료 후 이 API만 호출하면 됨.
---
✔ [4] 설문 상태 조회 (메인 화면에서 필요한 API)
GET /api/survey/status

백엔드는 CSV를 읽고 현재 유저(user_id)의 설문 상태를 반환합니다.
```
{
  "gds_completed": true,
  "memory_completed": false,
  "attention_completed": true,
  "language_completed": false,
  "draw_completed": false
}
```

프론트 메인에서 이 값 보고 UI에

- 완료 뱃지

- 진행중 표시

- 미시작 표시 
등을 렌더링하면 됩니다.
---
📌 4. 프론트가 구현해야 하는 것
✔ 1) 페이지 첫 로딩 시 /api/init 한 번 호출

→ user_id 쿠키 저장
→ 이후 API는 자동으로 user_id 포함됨

✔ 2) 설문을 “시작”할 때
```
POST /api/survey/start?type=gds
```

✔ 3) 설문 완료 후
```
POST /api/survey/complete?type=gds
```

✔ 4) 메인 화면에서 설문 UI 표시할 때
```
GET /api/survey/status
```

받은 JSON으로 카드 UI의 “체크/진행중/미시작” 표시
---
📌 5. 프론트에서 알 필요 없는 것 (백엔드 전용)

CSV 파일의 위치/포맷

파일 read/write 구조

파일 Lock 처리

UUID 생성 방식

백엔드 내부 switch문 로직

➡ 프론트는 “API 인터페이스”만 알면 됨.
---
📌 API 명세서 요약본 (프론트 전달용)
사용자 초기화

GET /api/init

Response:
Set-Cookie: user_id=xxxxx
---
설문 시작

POST /api/survey/start?type={gds|memory|attention|language|draw}
```
"started"
```

설문 완료

POST /api/survey/complete?type={gds|memory|attention|language|draw}

```
"completed"
```

현재 사용자 설문 상태 조회

GET /api/survey/status
```
{
  "gds_completed": true,
  "memory_completed": false,
  "attention_completed": true,
  "language_completed": false,
  "draw_completed": false
}
```
