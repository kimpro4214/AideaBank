📌 GET /surveys/status — 설문 상태 조회 API 명세서

🟦 HTTP Request

GET api/surveys/status

Headers

| Key       | Value  | 설명                     |
| --------- | ------ | ---------------------- |
| `user-id` | string | 유저 식별용 ID (쿠키로 관리해도 됨) |

🟥 Response (200 OK)

백엔드는 아래 JSON을 반환한다:

```
{
  "basic_status": "completed",
  "mmse_status": "completed",
  "gds_status": "completed",
  "gds_score": 30,
  "mmse_time_score": 5,
  "mmse_registration_score": 3,
  "mmse_recall_score": 2,
  "mmse_attention_score": 4,
  "mmse_language_score": 8,
  "mmse_copy_score": 1
}
```

🟩 Response 필드 설명

| 필드명                       | 타입     | 설명                                               |
| ------------------------- | ------ | ------------------------------------------------ |
| `basic_status`            | string | 기본 설문 상태 (`non-start` / `started` / `completed`) |
| `mmse_status`             | string | MMSE 설문 상태                                       |
| `gds_status`              | string | GDS 설문 상태                                        |
| `gds_score`               | number | GDS 총점                                           |
| `mmse_time_score`         | number | MMSE 시간 지남력 점수                                   |
| `mmse_registration_score` | number | MMSE 기억 등록(단어 기억) 점수                             |
| `mmse_recall_score`       | number | MMSE 기억 회상(단어 회상) 점수                             |
| `mmse_attention_score`    | number | MMSE 주의집중/계산 점수                                  |
| `mmse_language_score`     | number | MMSE 언어 기능 점수                                    |
| `mmse_copy_score`         | number | MMSE 도형 모사 점수                                    |


