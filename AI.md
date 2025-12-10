📘 🧠 AI 기반 치매 예측 서비스 – API 명세서

본 문서는 클라이언트(프론트엔드)가 서버와 연동하기 위한 전체 설문 API + AI 진단 API 명세서입니다.
서비스는 다음 3개의 설문을 기반으로 합니다.

기본 설문(Basic Survey)

MMSE 설문(Mini Mental State Exam)

GDS 설문(Geriatric Depression Scale)

이 3가지를 완료하면 /api/ai/diagnose API를 호출해서
AI 예측 결과 + Gemini 소견서를 받을 수 있습니다.

---
#1. 사용자 초기화 API

📌 GET api/surveys/init
설명

유저 식별을 위한 user_id 쿠키를 발급하는 초기 API.

Response
```
"user initialized"
```

Cookie 생성
```
user_id = {UUID}
```

---
#2. 설문 상태 조회 API

📌 GET api/surveys/status

설명:

사용자가 진행한 모든 설문 상태 및 점수를 조회합니다.

Response 예시
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
  "mmse_copy_score": 1,

  "basic_survey": {
    "age_cognition": 70,
    "sex": 1,
    "race": 0,
    "education": 12
  },

  "mmse_scores": {
    "mmse-1": 1,
    "mmse-2": 1,
    "mmse-3": 1,
    "mmse-4": 3,
    "mmse-5": 3,
    "mmse-6": 5,
    "mmse-7": 1,
    "mmse-8": 3,
    "mmse-9": 1,
    "mmse-10": 1,
    "mmse-11": 1,
    "mmse-12": 1
  }
}
```
---
#3. 설문 시작 API

📌 GET api/surveys/start?type={basic|mmse|gds}

설명

각 설문을 시작할 때 호출.

Response
```
"started"
```

---

#4. 설문 완료 API

모든 설문 완료 API는 다음 형식을 사용합니다.

GET api/surveys/complete?type=basic
GET api/surveys/complete?type=mmse
GET api/surveys/complete?type=gds

---

#5. AI 통합 진단 API

📌 POST /api/ai/diagnose

설명

BASIC 설문 CSV 생성

MMSE CSV 생성

두 CSV를 AI 서버로 전달

AI 결과를 기반으로 Gemini가 소견서를 생성

예측 + 소견서 반환

🎯 diagnose API가 실행되기 위한 조건

| 설문    | 완료 여부     |
| ----- | --------- |
| Basic | completed |
| MMSE  | completed |
| GDS   | completed |

완료되지 않으면 다음 에러 반환:
```
"설문 데이터가 부족합니다."
```

Response
```
{
  "model_result": {
    "diagnosis": "MCI",
    "confidence": 0.82,
    "llm_prompt": "환자의 MMSE 점수는 ..."
  },
  "llm_report": "Gemini가 생성한 최종 소견 텍스트"
}
```

