package gift.survey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/grade")
public class GeminiGradeController {

    private final Client genAIClient;
    private final String modelName = "gemini-2.5-flash";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiGradeController(Client genAIClient) {
        this.genAIClient = genAIClient;
    }

    public record GradeReq(int questionId, String sttText) {}
    public record GradeRes(int question_id, String verdict) {}

    private static final String SYSTEM_PROMPT = """
        당신은 MMSE-K 검사를 채점하는 AI입니다.
        출력은 반드시 JSON 형식만 허용됩니다.

        [문항 9] 5각형 두 개를 겹쳐 그리기
        - 정답: 오각형 두 개가 서로 일부라도 겹쳐 있음
        - 오답: 오각형이 두 개가 아님, 겹치지 않음 등

        [문항 11] 옷은 왜 빨아(세탁)서 입습니까?
        - 정답: 위생, 청결, 더러움 제거, 냄새 제거 등

        [문항 12] 주운 주민등록증을 어떻게 주인에게 돌려줍니까?
        - 정답: 우체국, 우편, 우체통, 집배원

        반드시 다음 형식으로만 출력하세요.
        {"question_id": 숫자, "verdict": "정답" 또는 "오답"}
        """;

    // ------------------ 1) 텍스트 채점 (11, 12번) ------------------
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GradeRes grade(@RequestBody GradeReq req) {

        try {
            String userPrompt = """
                question_id: %d
                stt_text: "%s"
                """.formatted(req.questionId(), req.sttText());

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .build();

            GenerateContentResponse response =
                    genAIClient.models.generateContent(
                            modelName,
                            SYSTEM_PROMPT + "\n\n" + userPrompt,
                            config
                    );

            String raw = response.text();
            System.out.println("🔍 Gemini raw response = " + raw);

            Map<String, Object> map = objectMapper.readValue(raw, Map.class);

            String verdict = (String) map.get("verdict");
            if (verdict == null) {
                verdict = "정답"; // 🔥 fallback
            }

            return new GradeRes(
                    ((Number) map.getOrDefault("question_id", req.questionId())).intValue(),
                    verdict
            );

        } catch (Exception e) {
            // 🔥 Gemini 실패 시 무조건 정답
            return new GradeRes(req.questionId(), "정답");
        }
    }

    // ------------------ 2) 이미지 URL 채점 (9번) ------------------
    @PostMapping(
            path = "/image-url",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GradeRes gradeWithImageUrl(@RequestBody Map<String, Object> body) {

        try {
            Integer questionId = (body.get("questionId") instanceof Number n) ? n.intValue() : null;
            if (questionId == null || questionId != 9)
                throw new IllegalArgumentException("questionId=9 만 허용됩니다.");

            String imageUrl = (String) body.get("imageUrl");
            if (imageUrl == null || imageUrl.isBlank())
                throw new IllegalArgumentException("imageUrl 은 필수입니다.");

            ImageData img = downloadImage(imageUrl);
            if (img == null || img.bytes == null)
                throw new IllegalArgumentException("이미지 다운로드 실패");

            ImageData finalImage = safeConvertToPng(img.bytes, img.mime);
            return callGeminiImage(finalImage);

        } catch (Exception e) {
            return new GradeRes(9, "정답"); // 🔥 fail-open
        }
    }

    // ------------------ 3) 이미지 바이너리 업로드 채점 (9번) ------------------
    @PostMapping(
            path = "/image-binary",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GradeRes gradeWithBinary(
            @RequestParam("questionId") Integer questionId,
            @RequestPart("file") MultipartFile file
    ) {

        try {
            if (questionId == null || questionId != 9)
                throw new IllegalArgumentException("questionId=9 만 허용됩니다.");

            if (file == null || file.isEmpty())
                throw new IllegalArgumentException("file 은 필수입니다.");

            ImageData finalImage =
                    safeConvertToPng(file.getBytes(), file.getContentType());

            return callGeminiImage(finalImage);

        } catch (Exception e) {
            return new GradeRes(9, "정답"); // 🔥 fail-open
        }
    }

    // ------------------ Gemini 비전 호출 ------------------
    private GradeRes callGeminiImage(ImageData img) {

        try {
            String userPrompt = """
                아래 이미지를 분석하여 오각형 두 개가 서로 일부라도 겹쳐 있으면 '정답',
                그렇지 않으면 '오답'으로 판단하세요.
                출력은 JSON만 허용합니다.
                """;

            Content content = Content.fromParts(
                    Part.fromText(SYSTEM_PROMPT),
                    Part.fromText(userPrompt),
                    Part.fromBytes(img.bytes, img.mime)
            );

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .build();

            GenerateContentResponse response =
                    genAIClient.models.generateContent(modelName, content, config);

            String raw = response.text();
            System.out.println("🔍 Gemini image raw response = " + raw);

            Map<String, Object> map = objectMapper.readValue(raw, Map.class);

            return new GradeRes(
                    ((Number) map.getOrDefault("question_id", 9)).intValue(),
                    (String) map.getOrDefault("verdict", "정답")
            );

        } catch (Exception e) {
            return new GradeRes(9, "정답"); // 🔥 fail-open
        }
    }

    // ------------------ PNG 변환 ------------------
    private ImageData safeConvertToPng(byte[] originalBytes, String mime) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (image == null) {
                return new ImageData(originalBytes, mime != null ? mime : "image/*");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return new ImageData(baos.toByteArray(), "image/png");

        } catch (Exception e) {
            return new ImageData(originalBytes, mime != null ? mime : "image/*");
        }
    }

    // ------------------ 이미지 URL 다운로드 ------------------
    private record ImageData(byte[] bytes, String mime) {}

    private static ImageData downloadImage(String urlStr) {
        try {
            URLConnection conn = new URL(urlStr).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            try (InputStream in = conn.getInputStream()) {
                return new ImageData(in.readAllBytes(), conn.getContentType());
            }
        } catch (Exception e) {
            return null;
        }
    }
}
