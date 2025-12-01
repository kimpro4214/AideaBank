🧩 0. 사용자 식별 — 익명 user_id 발급
📌 GET api/surveys/init
Request

없음

Response (200 OK)

응답과 함께 user_id 쿠키 발급됨.

```
"user initialized"
```

---
🧩 1. 설문 시작 / 완료 (공통)
✔ 설문 시작

📌 GET /surveys/start?type={basic|mmse|gds}

Query Parameters

| name | desc  | allowed          |
| ---- | ----- | ---------------- |
| type | 설문 종류 | basic, mmse, gds |

Response (200 OK)

```
"started"
```

---
✔ 설문 완료

📌 GET /surveys/complete?type={basic|mmse|gds}

Query Parameters

| name | desc  | allowed          |
| ---- | ----- | ---------------- |
| type | 설문 종류 | basic, mmse, gds |

Response (200 OK)

```
"completed"
```
