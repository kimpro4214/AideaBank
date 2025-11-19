바이너리 이미지 업로드 채점 API

POST /grade/image-binary

📥 Request (multipart/form-data)

| 필드           | 타입   | 설명              |
| ------------ | ---- | --------------- |
| `questionId` | text | 반드시 9           |
| `file`       | file | 이미지 파일 (binary) |

📤 Response (200 OK)
```
json
{
  "question_id": 9,
  "verdict": "정답"
}
```

