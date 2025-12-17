📌 POST /api/surveys/basic — 기본 설문 저장

✔ Request

```
{
  "age_cognition": 3,
  "sex": 1,
  "race": 2,
  "education": 4
}
```

필드 설명

| 필드              | 타입     | 설명       |
| --------------- | ------ | -------- |
| `age_cognition` | number | 인지 연령 구분 |
| `sex`           | number | 성별 코드    |
| `race`          | number | 인종 코드    |
| `education`     | number | 교육 수준 코드 |

✔ Response (200 OK)

```
{ "status": "saved" }
```
---

기본 설문 조회 (BASIC)

GET /api/surveys/basic

저장된 Basic 설문 데이터를 조회하는 API.

Response (200 OK)
```
{
  "age_cognition": 70,
  "sex": 1,
  "race": 0,
  "education": 12
}
```

