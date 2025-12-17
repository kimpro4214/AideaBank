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
        "success": true,
        "llm_prompt": "\n[상황]\n당신은 AI 진단 보조 시스템입니다. 환자 데이터에 대한 AI 분석 결과입니다.\n\n# 1. 환자 정보\n{'PHC_Age_Cognition': 3.0, 'PHC_Sex': 1.0, 'PHC_Race': 2.0, 'PHC_Education': 4.0, 'PHC_MEM': 0.9137178135822918, 'PHC_EXF': 0.549023221525986, 'PHC_VSP': 1.0259197324414715, 'PHC_LAN': 0.734273694630042}\n\n# 2. RAG 검색 및 분석\n- 예측 방식: RAG 앙상블\n- 유사 환자 수: 5명 (MRI 기반 예측에 사용)\n\n# 3. AI 최종 소견\n- 예측 진단: [CN]\n- 확률: CN(78.3%), MCI(21.0%), AD(0.7%)\n\n[요청]\n위 데이터를 바탕으로 최종 소견서를 작성해주세요.\n",
        "ensemble_result": null,
        "statistical_result": {
            "final_diagnosis": "CN",
            "ptids": [
                "128_S_0229",
                "068_S_2184",
                "031_S_4021",
                "011_S_0008",
                "002_S_4213"
            ],
            "count": 5,
            "probabilities": {
                "CN": 0.7834183037281036,
                "MCI": 0.20979325119405984,
                "AD": 0.006788445077836514
            },
            "method": "RAG Ensemble"
        }
    },
    "llm_report": "## AI 진단 보조 시스템 최종 소견서\n\n**발신:** AI 진단 보조 시스템\n**수신:** 의료 전문가 / 담당의\n**날짜:** 2023년 10월 27일\n\n---\n\n**1. 환자 정보 요약**\n\n제출된 환자 정보에 대한 AI 분석이 진행되었습니다. 여기에는 인지 기능과 관련된 연령(PHC_Age_Cognition: 3.0), 성별(PHC_Sex: 1.0), 인종(PHC_Race: 2.0), 학력(PHC_Education: 4.0) 등의 기본 정보 및 PHC_MEM(0.9137), PHC_EXF(0.5490), PHC_VSP(1.0259), PHC_LAN(0.7342)을 포함한 다양한 인지 평가 지표들이 활용되었습니다.\n\n**2. AI 분석 방법론**\n\n본 진단은 RAG(Retrieval-Augmented Generation) 앙상블 예측 방식을 사용하여 수행되었습니다. 특히, AI는 환자의 데이터와 유사한 특징을 가진 5명의 환자 데이터를 MRI 기반 예측에 참조하여 정교한 분석을 진행하였습니다.\n\n**3. AI 최종 소견**\n\nAI 분석 결과, 환자분의 예측 진단은 **[CN (정상 인지)]**으로 나타났습니다. 각 진단 범주별 확률은 다음과 같습니다:\n\n*   **CN (정상 인지): 78.3%**\n*   **MCI (경도 인지 장애): 21.0%**\n*   **AD (알츠하이머병): 0.7%**\n\n**해석:**\n\n주요 소견은 **'정상 인지(CN)'**로, AI는 환자가 현재 인지적으로 정상 범위에 있을 가능성이 가장 높다고 판단합니다.\n\n다만, **'경도 인지 장애(MCI)'**의 가능성도 21.0%로 나타나, 비록 주된 소견은 아니지만 잠재적인 인지 기능 변화에 대한 추가적인 관심과 관찰이 필요할 수 있습니다. 알츠하이머병(AD)의 가능성은 0.7%로 매우 낮게 예측되었습니다.\n\n**4. 중요 고지사항 및 권고**\n\n본 소견은 AI 진단 보조 시스템의 분석 결과이며, 최종적인 의학적 진단으로 간주될 수 없습니다. 환자의 임상 증상, 신경학적 검사 결과, 추가 영상 자료 및 기타 의료 정보를 종합적으로 고려하여 의료 전문가에 의한 최종 진단 및 치료 계획이 수립되어야 합니다.\n\n---\n**AI 진단 보조 시스템 드림**"
}
```

