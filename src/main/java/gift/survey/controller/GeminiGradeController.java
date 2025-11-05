package gift.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/grade")
public class GeminiGradeController {

    private final Client genAIClient;
    private final String modelName = "gemini-2.5-flash";

    public GeminiGradeController() {
        this.genAIClient = new Client(); // GEMINI_API_KEY 자동 로드
    }

    // === DTO ===
    public record GradeReq(int questionId, String sttText) {}
    public record GradeRes(int question_id, String verdict) {}

    // 시스템 프롬프트
    private static final String SYSTEM_PROMPT = """
        당신은 MMSE-K 검사를 채점하는 AI입니다. JSON만 출력하세요.

        [문항 9] "5각형 두 개를 겹쳐 그리기"
        - 정답: 이미지 상에 '오각형 두 개'가 인접/부분적으로 겹쳐 있는 도형으로 그려져 있으면 정답.
        - 오답: 오각형 수가 두 개가 아님, 삼각형/사각형 등 다른 도형, 서로 겹치지 않음, 난화 등.

        [문항 11] "옷은 왜 빨아(세탁)서 입습니까?"
        - 정답 의미: 위생/청결/더러움 제거/냄새 제거/깨끗하게 하려고 등. 그 외는 오답.

        [문항 12] "길에서 남의 주민등록증을 주웠을 때, 어떻게 하면 쉽게 주인에게 되돌려 줄 수 있겠습니까?"
        - 정답: 우체국/우편/우체통/집배원 언급. 그 외(경찰서/주민센터/SNS/전화 등)는 오답.

        출력 형식은 JSON 하나만:
        {"question_id": 11, "verdict": "정답"} 또는 {"question_id": 9, "verdict": "오답"}

        JSON 외 다른 설명, 문장, 마크다운 등 절대 포함하지 마세요.
        """;

    // ========= 1) 텍스트 채점 =========
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GradeRes grade(@RequestBody GradeReq req) throws Exception {
        String userPrompt =
                "question_id: " + req.questionId() + "\n" +
                        "stt_text: \"\"\"" + req.sttText() + "\"\"\"";

        String fullPrompt = SYSTEM_PROMPT + "\n\n" + userPrompt;

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response = genAIClient.models
                .generateContent(this.modelName, fullPrompt, config);

        String json = response.text().trim();
        Map<?, ?> map = new ObjectMapper().readValue(json, Map.class);
        int qid = ((Number) map.get("question_id")).intValue();
        String verdict = (String) map.get("verdict");
        return new GradeRes(qid, verdict);
    }

    // ========= 2) 이미지 채점 (URL 기반) =========
    // ========= 2-1) 이미지 채점 (URL, JSON 바디) =========
    @PostMapping(
            path = "/image-url",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GradeRes gradeWithImageUrl(@RequestBody Map<String, Object> body) throws Exception {
        Integer questionId = body.get("questionId") == null ? null : ((Number) body.get("questionId")).intValue();
        String imageUrl = (String) body.get("imageUrl");

        if (questionId == null || questionId != 9) {
            throw new IllegalArgumentException("questionId=9 만 이미지 채점 엔드포인트에서 허용됩니다.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl 은 필수입니다.");
        }

        String userPrompt = """
        question_id: 9
        instruction: 첨부된 이미지가 '오각형 두 개가 서로 일부 겹친 도형'이면 정답, 아니면 오답.
        출력은 {"question_id": 9, "verdict": "정답" 또는 "오답"} JSON 하나만.
        """;

        // 🔁 URL → 바이트 다운로드 후 업로드
        ImageData img = downloadImage(imageUrl);
        if (img == null || img.bytes == null || img.bytes.length == 0) {
            throw new IllegalArgumentException("이미지 다운로드 실패(접근 불가/빈 파일)");
        }
        String mime = (img.mime != null && !img.mime.isBlank())
                ? img.mime : guessImageMime(imageUrl);
        if (mime == null) {
            throw new IllegalArgumentException("이미지 MIME 판별 실패(jpg/jpeg/png/webp 권장)");
        }

        Content content = Content.fromParts(
                Part.fromText(SYSTEM_PROMPT),
                Part.fromText(userPrompt),
                Part.fromBytes(img.bytes, mime)   // ← 핵심: fromBytes 로 전달
        );

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response = genAIClient.models
                .generateContent(this.modelName, content, config);

        String json = response.text();
        if (json == null || json.isBlank()) throw new IllegalStateException("모델 응답이 비었습니다.");

        Map<String, Object> map = new ObjectMapper()
                .readValue(json.trim(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>(){});
        int qid = (map.get("question_id") instanceof Number n) ? n.intValue() : 9;
        String verdict = String.valueOf(map.getOrDefault("verdict", "오답"));
        return new GradeRes(qid, verdict);
    }

    private static class ImageData {
        final byte[] bytes;
        final String mime;
        ImageData(byte[] b, String m) { this.bytes = b; this.mime = m; }
    }

    private static ImageData downloadImage(String urlStr) {
        try {
            java.net.URL url = new java.net.URL(urlStr);
            java.net.URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            String contentType = conn.getContentType(); // 서버가 주면 사용
            try (java.io.InputStream in = conn.getInputStream()) {
                byte[] data = in.readAllBytes(); // Java 11+
                // contentType이 비면 시그니처/확장자로 보정
                if (contentType == null || contentType.isBlank()) {
                    contentType = sniffMime(data);
                }
                return new ImageData(data, contentType);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static String sniffMime(byte[] data) {
        if (data == null || data.length < 12) return null;
        // PNG
        byte[] png = new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10};
        boolean isPng = true;
        for (int i = 0; i < png.length; i++) if (data[i] != png[i]) { isPng = false; break; }
        if (isPng) return "image/png";
        // JPEG (FF D8 ..)
        if ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8) return "image/jpeg";
        // WEBP (RIFF....WEBP)
        if (data[0]=='R' && data[1]=='I' && data[2]=='F' && data[3]=='F'
                && data[8]=='W' && data[9]=='E' && data[10]=='B' && data[11]=='P') return "image/webp";
        return null;
    }

    private static String guessImageMime(String url) {
        if (url == null) return null;
        String u = url.toLowerCase();
        int q = u.indexOf('?'); if (q >= 0) u = u.substring(0, q);
        int h = u.indexOf('#'); if (h >= 0) u = u.substring(0, h);
        if (u.endsWith(".jpg") || u.endsWith(".jpeg")) return "image/jpeg";
        if (u.endsWith(".png")) return "image/png";
        if (u.endsWith(".webp")) return "image/webp";
        return null;
    }
}
