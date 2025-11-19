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

    public GeminiGradeController(Client genAIClient) {
        this.genAIClient = genAIClient;
    }

    public record GradeReq(int questionId, String sttText) {}
    public record GradeRes(int question_id, String verdict) {}

    private static final String SYSTEM_PROMPT = """
        당신은 MMSE-K 검사를 채점하는 AI입니다. JSON만 출력하세요.

        [문항 9] "5각형 두 개를 겹쳐 그리기"
        - 정답: 이미지 상에 '오각형 두 개'가 인접/부분적으로 겹쳐 있는 도형으로 그려져 있으면 정답.
        - 오답: 오각형 수가 두 개가 아님, 삼각형/사각형 등 다른 도형, 서로 겹치지 않음, 난화 등.

        [문항 11] "옷은 왜 빨아(세탁)서 입습니까?"
        - 정답: 위생/청결/더러움 제거/냄새 제거/깨끗하게 하기 등

        [문항 12] "주운 주민등록증을 어떻게 주인에게 돌려줍니까?"
        - 정답: 우체국/우편/우체통/집배원

        JSON만 출력하세요.
        """;


    // ------------------ 1) 텍스트 채점 ------------------
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GradeRes grade(@RequestBody GradeReq req) throws Exception {

        String userPrompt = """
            question_id: %d
            stt_text: \"%s\"
            """.formatted(req.questionId(), req.sttText());

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response =
                genAIClient.models.generateContent(modelName, SYSTEM_PROMPT + "\n\n" + userPrompt, config);

        Map<?, ?> map = new ObjectMapper().readValue(response.text().trim(), Map.class);
        return new GradeRes(((Number) map.get("question_id")).intValue(), (String) map.get("verdict"));
    }


    // ------------------ 2) 이미지 URL 채점 ------------------
    @PostMapping(
            path = "/image-url",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GradeRes gradeWithImageUrl(@RequestBody Map<String, Object> body) throws Exception {

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
    }


    // ------------------ 3) 바이너리 업로드 채점 (핵심) ------------------
    @PostMapping(
            path = "/image-binary",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GradeRes gradeWithBinary(
            @RequestParam("questionId") Integer questionId,
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        if (questionId == null || questionId != 9)
            throw new IllegalArgumentException("questionId=9 만 허용됩니다.");

        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("file 은 필수입니다.");

        byte[] originalBytes = file.getBytes();
        String mime = file.getContentType();

        ImageData finalImage = safeConvertToPng(originalBytes, mime);

        return callGeminiImage(finalImage);
    }


    // ------------------ Gemini 비전 모델 호출 공통 ------------------
    private GradeRes callGeminiImage(ImageData img) throws Exception {

        String userPrompt = """
            아래 이미지를 분석하여 오각형(꼭짓점 5개)이 두 개 존재하는지 확인하고,
            두 오각형이 서로 일부라도 겹치면 '정답', 그 외는 '오답'으로 판단하세요.
        
            출력은 {"question_id": 9, "verdict": "정답"} 또는 {"question_id": 9, "verdict": "오답"} 형식만 허용합니다.
            추가 설명 금지.
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

        String json = response.text().trim();
        Map<String, Object> map = new ObjectMapper()
                .readValue(json, new ObjectMapper().getTypeFactory().constructMapType(Map.class, String.class, Object.class));

        return new GradeRes(
                ((Number) map.getOrDefault("question_id", 9)).intValue(),
                (String) map.getOrDefault("verdict", "오답")
        );
    }


    // ------------------ 안전 PNG 변환: 실패 시 원본 그대로 ------------------
    private ImageData safeConvertToPng(byte[] originalBytes, String mime) {

        try {
            // Base64 문자열 업로드 자동 감지
            String asString = new String(originalBytes).trim();
            if (asString.matches("^[A-Za-z0-9+/=\\s]+$") && asString.length() % 4 == 0) {
                System.out.println("⚠ Base64 detected → decoding");
                originalBytes = Base64.getDecoder().decode(asString);
                mime = "image/*";
            }

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (image == null) {
                System.out.println("⚠ PNG 변환 실패 → fallback");
                return new ImageData(originalBytes, mime != null ? mime : "image/*");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            byte[] pngBytes = baos.toByteArray();

            // ⭐ PNG 파일 저장 기능 추가 ⭐
            try {
                String savedPath = savePngToDisk(pngBytes, "converted_" + System.currentTimeMillis() + ".png");
                System.out.println("PNG 저장됨: " + savedPath);
            } catch (IOException e) {
                System.out.println("PNG 저장 실패: " + e.getMessage());
            }

            return new ImageData(pngBytes, "image/png");

        } catch (Exception e) {
            System.out.println("⚠ PNG 변환 오류 → fallback");
            return new ImageData(originalBytes, mime != null ? mime : "image/*");
        }
    }



    private String savePngToDisk(byte[] pngBytes, String filename) throws IOException {
        // 저장 디렉토리 지정 (원한다면 application.yml로 빼도 됨)
        String outputDir = System.getProperty("user.dir") + "/saved-png";

        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        File outputFile = new File(dir, filename);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(pngBytes);
        }

        return outputFile.getAbsolutePath(); // 저장된 파일 경로 반환
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
