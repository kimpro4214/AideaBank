package gift.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Gemini API 기반 자동 채점 컨트롤러 (com.google.genai:google-genai:1.25.0 기준)
 * 질문 9, 11, 12번용 (멀티모달 제외)
 *
 * build.gradle 예)
 *   implementation 'com.google.genai:google-genai:1.25.0'
 *
 * 실행 전 환경변수 필요:
 *   GOOGLE_API_KEY=AIza...        // 구 SDK에서는 GEMINI_API_KEY도 되지만
 *                                 // google-genai Client()는 GOOGLE_API_KEY 우선 사용
 */
@RestController
@RequestMapping("/grade")
public class GeminiGradeController {

    private final Client genAIClient;              // Google GenAI Java SDK 클라이언트
    private final String modelName = "gemini-2.5-flash";

    public GeminiGradeController() {
        // 기본 생성자는 환경변수(API Key)를 자동으로 읽는다.
        // (GOOGLE_API_KEY 또는 GEMINI_API_KEY 중 하나)
        this.genAIClient = new Client();
    }

    // === 요청/응답 DTO ===
    public record GradeReq(int questionId, String sttText) {}
    public record GradeRes(int question_id, String verdict) {}

    // 시스템 프롬프트 (룰 + 출력 형식 강제)
    private static final String SYSTEM_PROMPT = """
        당신은 MMSE-K 검사를 채점하는 AI입니다. JSON만 출력하세요.

        각 문항별 채점 규칙은 다음과 같습니다.

        [문항 9] "5각형 두 개를 겹쳐 그리기"
        - 그림 설명이 정확히 두 개의 오각형이 서로 일부 겹친다는 의미면 정답.
        - 예: "오각형 두 개 겹쳤다", "5각형 두 개 포개서 그림" 등 → 정답.
        - 오각형 수 잘못 말함, 삼각형/사각형 언급, 아무 말이나 함 → 오답.

        [문항 11] "옷은 왜 빨아(세탁)서 입습니까?"
        - 정답 의미: 위생, 청결, 더러움 제거, 냄새 제거, 깨끗하게 하려고 등.
        - 예: "깨끗하게 입으려고", "더러워서", "청결 유지하려고" → 정답.
        - 오답: 패션/습관/엄마가 시켜서/이상한 대답 → 오답.

        [문항 12] "길에서 남의 주민등록증을 주웠을 때, 어떻게 하면 쉽게 주인에게 되돌려 줄 수 있겠습니까?"
        - 정답 의미: 우체국/우편/우체통/집배원 언급 시 정답.
        - 예: "우체국에 맡긴다", "우체통에 넣는다" → 정답.
        - 경찰서/주민센터/SNS/전화 등 → 오답.
        - 합리적으로 보여도 우편 관련 아니면 오답.

        출력 형식은 JSON 하나만:
        {"question_id": 11, "verdict": "정답"}
        또는
        {"question_id": 9, "verdict": "오답"}

        JSON 외 다른 설명, 문장, 마크다운 등 절대 포함하지 마세요.
        """;

    @PostMapping
    public GradeRes grade(@RequestBody GradeReq req) throws Exception {
        // 사용자 발화 -> 모델 판단용 프롬프트 구성
        String userPrompt =
                "question_id: " + req.questionId() + "\n" +
                        "stt_text: \"\"\"" + req.sttText() + "\"\"\"";

        String fullPrompt = SYSTEM_PROMPT + "\n\n" + userPrompt;

        // 모델에게 "JSON만 줘"라고 강하게 요구
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        // google-genai 1.25.0 스타일:
        //  - genAIClient.models 는 필드 (메서드 아님)
        //  - generateContent(modelName, prompt, config)
        GenerateContentResponse response = genAIClient
                .models
                .generateContent(this.modelName, fullPrompt, config);

        String json = response.text().trim(); // 모델 응답(순수 JSON 기대)

        // JSON 파싱
        Map<?, ?> map = new ObjectMapper().readValue(json, Map.class);

        int qid = ((Number) map.get("question_id")).intValue();
        String verdict = (String) map.get("verdict");

        return new GradeRes(qid, verdict);
    }
}
