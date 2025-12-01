📘 GDS 우울 척도 점수 계산 API

POST /api/gds/score

📥 Request (application/json)

| 필드       | 타입     | 설명                |
| -------- | ------ | ----------------- |
| `{문항번호}` | number | 0 또는 1 (비우울 / 우울) |

```
{
  "1": 1,
  "2": 0,
  "3": 1
}
```

📤 Response (200 OK)
```
{
  "score": 12,
  "level": "중등도 우울",
  "depressionRisk": true
}
```

---

📘 GDS 점수 기반 AI 예측 API

POST /api/gds/predict

📥 Request (application/json)

| 필드       | 타입     | 설명     |
| -------- | ------ | ------ |
| `{문항번호}` | number | 0 또는 1 |

```
{
  "1": 1,
  "2": 0,
  "3": 1
}
```

📤 Response (200 OK)

```
{
  "gds_score": 15,
  "model_pred": "고위험군",
  "recommendation": "병원 내원 필요"
}
```