# 📘 MMSE-K Score Collection API

프론트엔드에서 계산된 MMSE-K 점수를 백엔드로 전달하고,  
백엔드는 데이터를 날짜별 CSV 파일로 저장하여 AI 전처리/모델링 팀이 사용할 수 있도록 관리하는 API입니다.

---

## 🚀 Base URL

http://98.81.237.57:8080/api/mmse


---

# 📌 Endpoints

## 1) 제출된 MMSE 점수 저장
### **POST http://98.81.237.57:8080/api/mmse/score**

프론트에서 자동 채점한 MMSE-K 점수를 서버로 전송합니다.  
서버는 데이터를 날짜별 CSV 파일로 저장합니다.

---

### 📢 Request

#### Headers
Content-Type: application/json

#### Body (JSON)

```json
{
  "userId": 123,
  "scores": {
    "mmse-1": 4,
    "mmse-4": 3,
    "mmse-5": 2,
    "mmse-6": 3,
    "mmse-7": 2,
    "mmse-9": 1,
    "mmse-10": 1,
    "mmse-11": 1,
    "mmse-12": 1
  },
  "totalScore": 18
}
```

| Field        | Type                 | Description                  |
| ------------ | -------------------- | ---------------------------- |
| `userId`     | Long                 | 사용자 고유 ID                    |
| `scores`     | Map<String, Integer> | MMSE 항목별 점수 (`mmse-{번호}` 형식) |
| `totalScore` | Integer              | 전체 총점                        |

📤 Response (200 OK)
{
  "status": "OK",
  "message": "MMSE score received and stored to CSV",
  "userId": 123
}

📁 CSV 저장 규칙
점수가 수신되면 서버는 다음 경로에 CSV 파일을 생성합니다:
/uploads/mmse/export/MMSE_YYYY-MM-DD.csv

-----------------------------------------------------------
# 📘 Demographics Data Collection API
프론트엔드에서 입력된 인구통계(핵심 PHC 변수)를 백엔드로 전달하고,  
백엔드는 데이터를 날짜별 CSV 파일로 저장하여 AI 전처리/모델링 팀이 사용할 수 있도록 관리하는 API입니다.

---

## 🚀 Base URL
http://98.81.237.57:8080/api/demographics

---

# 📌 Endpoints

## 1) 제출된 인구통계 데이터 저장
### **POST /submit**

프론트에서 입력된 인구통계 항목을 서버로 전송합니다.  
서버는 해당 데이터를 날짜별 CSV 파일로 저장합니다.

---

### 📢 Request

#### Headers
Content-Type: application/json

#### Body (JSON)

```json
{
  "userId": 123,
  "PHC_Age_Cognition": 72,
  "PHC_Sex": 1,
  "PHC_Race": "Asian",
  "PHC_Education": 16,
  "PTHAND": "Right",
  "PTMARRY": "Married",
  "PTRTYR": 2018,
  "PTTLANG": "Korean",
  "PTPLANG": "Korean",
  "PTHOME": "With family"
}
```

📤 Response (200 OK)
```
json
{
"status": "OK",
"message": "Demographic data received and stored to CSV",
"userId": 123
}
```

-----------------------------------------------------------
# 🤖 Gemini 기반 MMSE-K 자동 채점 API

본 API는 MMSE-K 문항 중 **9번(그림)**, **11번(이해)**, **12번(판단)** 문항에 대해  
사용자의 입력(음성 → STT 텍스트 or 그림 이미지 URL)을 받아  
Google Gemini 모델을 통해 **정답 / 오답 판별**을 수행합니다.

판정 결과는 `"정답"` 또는 `"오답"` 으로 프론트엔드에 반환됩니다.  
프론트는 이 verdict를 기반으로 점수(1 or 0)를 계산합니다.

---

## 🚀 Base URL
http://98.81.237.57:8080/grade

---

# 💬 1) 텍스트 채점 API
MMSE-K **문항 11**, **문항 12** 전용

### **POST /grade**

사용자의 자연어 답변(STT 결과 텍스트)을 Gemini 모델에 전달하여  
정답/오답 여부를 판정합니다.

---

## 📥 Request

### Headers
Content-Type: application/json

### Body (JSON)
```json
{
  "questionId": 11,
  "sttText": "깨끗하게 하려고요"
}
```
| 필드           | 타입     | 설명                   |
| ------------ | ------ | -------------------- |
| `questionId` | int    | 채점할 문항 번호 (11 또는 12) |
| `sttText`    | string | 음성 인식 결과 텍스트         |

📤 Response (200 OK)
```
json
{
  "question_id": 11,
  "verdict": "정답"
}
```

| 필드            | 설명               |
| ------------- | ---------------- |
| `question_id` | 채점한 문항 번호        |
| `verdict`     | `"정답"` 또는 `"오답"` |

🖼 2) 이미지 채점 API
MMSE-K 문항 9(5각형 두 개 겹쳐 그리기) 전용

POST /grade/image-url

Gemini 모델이 이미지 URL을 다운로드하여
‘오각형 두 개가 겹쳐 있는 도형인지’ 자동 판단합니다.

📥 Request
Headers
Content-Type: application/json
```
json
{
"questionId": 9,
"imageUrl": "https://cdn.example.com/uploads/user123/drawing.png"
}
```

| 필드           | 타입     | 설명             |
| ------------ | ------ | -------------- |
| `questionId` | int    | 반드시 9만 허용      |
| `imageUrl`   | string | 채점할 그림 이미지 URL |

📤 Response (200 OK)
```
json
{
  "question_id": 9,
  "verdict": "오답"
}
```

📜 Gemini 채점 방식 (내부 로직)

Gemini에게 주어지는 시스템 프롬프트에는 다음 기준이 포함됨:

✔ 문항 9
오각형 두 개가 일부 겹치면 정답
오각형이 아닌 도형, 두 개가 아니면 오답

✔ 문항 11
위생/청결/깨끗함/더러움 제거 언급 → 정답

✔ 문항 12
우체국/우편/우체통 → 정답
경찰서/주민센터 등은 오답

Gemini는 JSON만 출력하도록 강제됨.

