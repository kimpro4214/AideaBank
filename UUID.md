🧩 0. 사용자 식별 — 익명 user_id 발급

📌 GET /surveys/init

Request

없음

Response (200 OK)

```
"user initialized"
```

---
🧩 1. 설문 시작 / 완료 (공통)

📌 POST /surveys/start?type={basic|mmse|gds}

Request

Query params:

| name | desc  | allowed          |
| ---- | ----- | ---------------- |
| type | 설문 종류 | basic, mmse, gds |

Response

```
"started"
```

---
📌 POST /surveys/complete?type={basic|mmse|gds}

Response

```
"completed"
```

