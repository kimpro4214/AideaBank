📌 GET /surveys/status — 설문 상태 조회 API 명세서

🟦 HTTP Request

GET api/surveys/status

Headers

| Key                   | Value  | 설명         |
| --------------------- | ------ | ---------- |
| `user-id` (또는 Cookie) | string | 익명 사용자 식별자 |


🟥 Response (200 OK)

백엔드는 아래 JSON을 반환한다:

```
{
  "basic_status": "completed",
  "mmse_status": "completed",
  "gds_status": "completed",
  "gds_score": 30,
  "mmse_scores": {
    "mmse-1": 4,
    "mmse-2": 0,
    "mmse-3": 0,
    "mmse-4": 3,
    "mmse-5": 2,
    "mmse-6": 3,
    "mmse-7": 2,
    "mmse-8": 0,
    "mmse-9": 1,
    "mmse-10": 1,
    "mmse-11": 1,
    "mmse-12": 1
  },
  "mmse_total": 18
}
```

🟩 Response 필드 설명

| 필드명            | 타입     | 설명                                                 |
| -------------- | ------ | -------------------------------------------------- |
| `basic_status` | string | 기본 설문 상태 (`non-start`, `in-progress`, `completed`) |
| `mmse_status`  | string | MMSE 설문 상태                                         |
| `gds_status`   | string | GDS 설문 상태                                          |
| `gds_score`    | number | GDS 총점                                             |

✔ mmse_scores (객체)

프론트에서 제출한 문항별 점수를 그대로 반환함

| 필드명       | 설명        |
| --------- | --------- |
| `mmse-1`  | 시간 지남력    |
| `mmse-2`  | 장소 지남력 ①  |
| `mmse-3`  | 장소 지남력 ②  |
| `mmse-4`  | 기억 등록     |
| `mmse-5`  | 기억 회상     |
| `mmse-6`  | 주의집중/계산   |
| `mmse-7`  | 사물 이름대기   |
| `mmse-8`  | 3단계 명령 수행 |
| `mmse-9`  | 도형 모사     |
| `mmse-10` | 문장 따라 말하기 |
| `mmse-11` | 이해        |
| `mmse-12` | 판단        |
