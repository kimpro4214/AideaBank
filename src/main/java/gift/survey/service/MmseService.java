package gift.survey.service;

import gift.survey.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.survey.util.CsvUtilMmse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MmseService {

    @Value("${mmse.upload-dir:${user.dir}/uploads/mmse}")
    private String uploadDir;

    @Value("${mmse.tts-base-url:https://cdn.example.com/audio/mmse}")
    private String ttsBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 🧠 질문 목록 반환
    public MmseQuestionResponse getQuestions() {
        List<MmseQuestionDto> list = List.of(
                new MmseQuestionDto(1L, "지남력", "오늘은 몇 년, 몇 월, 몇 일, 무슨 요일입니까?", ttsBaseUrl + "/q1.mp3", "TEXT"),
                new MmseQuestionDto(4L, "기억 등록", "물건 이름 세 가지를 말해보세요. (예: 나무, 자동차, 모자)", ttsBaseUrl + "/q4.mp3", "TEXT"),
                new MmseQuestionDto(6L, "주의 집중 및 계산", "100에서 7을 다섯 번 빼보세요. 또는 ‘삼천리강산’을 거꾸로 말하세요.", ttsBaseUrl + "/q6.mp3", "TEXT"),
                new MmseQuestionDto(8L, "언어 기능", "오른손으로 종이를 집어서 반으로 접어 무릎 위에 놓으세요.", ttsBaseUrl + "/q8.mp3", "VIDEO"),
                new MmseQuestionDto(9L, "시공간 구성", "오각형 두 개를 겹쳐 그려보세요.", ttsBaseUrl + "/q9.mp3", "IMAGE"),
                new MmseQuestionDto(10L, "언어 기능", "‘간장 공장 공장장’을 따라 말하세요.", ttsBaseUrl + "/q10.mp3", "AUDIO"),
                new MmseQuestionDto(11L, "이해", "옷은 왜 빨아서 입습니까?", ttsBaseUrl + "/q11.mp3", "TEXT"),
                new MmseQuestionDto(12L, "판단", "길에서 남의 주민등록증을 주웠을 때 어떻게 하면 쉽게 주인에게 돌려줄 수 있겠습니까?", ttsBaseUrl + "/q12.mp3", "TEXT")
        );

        return new MmseQuestionResponse("MMSE-K", list.size(), list);
    }

    // ⚙️ 자동 채점 (스테이지별 반환)
    private Map.Entry<String, Integer> autoScoreWithStage(String questionText, String answer) {
        if (answer == null || answer.isBlank()) return Map.entry("unknown", 0);
        questionText = questionText.trim();
        answer = answer.trim();

        // 지남력 (orientation)
        if (questionText.contains("몇 년") || questionText.contains("요일")) {
            int score = 0;
            if (answer.matches(".*\\d{4}.*")) score++;
            if (answer.contains("월")) score++;
            if (answer.contains("일")) score++;
            if (answer.contains("요일")) score++;
            return Map.entry("orientation", Math.min(score, 5));
        }

        // 기억 등록/회상 (memory)
        if (questionText.contains("물건 이름")) {
            String[] correct = {"나무", "자동차", "모자"};
            int cnt = 0;
            for (String c : correct) if (answer.contains(c)) cnt++;
            return Map.entry("memory", cnt);
        }

        // 주의 집중/계산 (attention)
        if (questionText.contains("100") || questionText.contains("삼천리강산")) {
            if (answer.matches(".*93.*86.*79.*72.*65.*")) return Map.entry("attention", 5);
            if (answer.contains("산강천리삼")) return Map.entry("attention", 3);
            return Map.entry("attention", 0);
        }

        // 언어 기능 (language)
        if (questionText.contains("간장 공장 공장장")) return Map.entry("language", 1);

        // 이해/판단 (judgment)
        if (answer.contains("깨끗")) return Map.entry("judgment", 1);
        if (answer.contains("우체국")) return Map.entry("judgment", 1);

        return Map.entry("unknown", 0);
    }

    // 🗂️ 응답 저장 + 자동 채점 + 스테이지별 점수 + 파일 업로드
    public MmseSubmitResponse saveResponse(Long userId, String answersJson, List<MultipartFile> files) {
        String userPath = uploadDir + "/" + userId;
        File dir = new File(userPath);
        if (!dir.exists()) dir.mkdirs();

        List<String> audioFiles = new ArrayList<>();
        List<String> videoFiles = new ArrayList<>();
        List<String> imageFiles = new ArrayList<>();

        int totalScore = 0;
        Map<String, Integer> stageScores = new HashMap<>(Map.of(
                "orientation", 0,
                "memory", 0,
                "attention", 0,
                "language", 0,
                "judgment", 0
        ));

        try {
            // 1️⃣ 파일 업로드 처리
            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.isEmpty() || file.getOriginalFilename() == null) continue;

                    Path dest = Path.of(userPath, file.getOriginalFilename());
                    Files.write(dest, file.getBytes());

                    String url = "https://cdn.example.com/uploads/" + userId + "/" + file.getOriginalFilename();
                    if (file.getOriginalFilename().endsWith(".mp3")) audioFiles.add(url);
                    else if (file.getOriginalFilename().endsWith(".mp4")) videoFiles.add(url);
                    else if (file.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) imageFiles.add(url);
                }
            }

            // 2️⃣ 자동 채점
            List<Map<String, Object>> answers = objectMapper.readValue(answersJson, List.class);
            for (Map<String, Object> a : answers) {
                String qText = (String) a.getOrDefault("questionText", "");
                String ans = (String) a.getOrDefault("answer", "");
                Map.Entry<String, Integer> scored = autoScoreWithStage(qText, ans);
                String stage = scored.getKey();
                int score = scored.getValue();

                if (stageScores.containsKey(stage)) {
                    stageScores.put(stage, stageScores.get(stage) + score);
                }
                totalScore += score;
            }

            // 3️⃣ 결과 저장
            Path scorePath = Path.of(userPath, "result.json");
            Map<String, Object> resultJson = Map.of(
                    "userId", userId,
                    "totalScore", totalScore,
                    "scoresByStage", stageScores,
                    "scoredAt", LocalDateTime.now().toString()
            );
            Files.writeString(scorePath, objectMapper.writeValueAsString(resultJson));

        } catch (IOException e) {
            log.error("파일 저장 또는 자동 채점 실패: {}", e.getMessage());
        }

        Map<String, List<String>> uploaded = Map.of(
                "audioFiles", audioFiles,
                "videoFiles", videoFiles,
                "imageFiles", imageFiles
        );

        return new MmseSubmitResponse(
                userId,
                "RECEIVED",
                "자동 채점 완료 (총점: " + totalScore + "점)",
                stageScores,
                uploaded
        );
    }

    // 📊 결과 조회 (총점 + 스테이지별 점수 반환)
    public MmseResultResponse getResult(Long userId) {
        String baseUrl = "https://cdn.example.com/uploads/" + userId + "/";
        List<String> attachments = new ArrayList<>();
        int score = 0;
        Map<String, Integer> stageScores = new HashMap<>();

        File dir = new File(uploadDir + "/" + userId);
        if (dir.exists()) {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.getName().endsWith(".mp3") || f.getName().endsWith(".mp4") || f.getName().matches(".*\\.(png|jpg|jpeg)$")) {
                    attachments.add(baseUrl + f.getName());
                }
                if (f.getName().equals("result.json")) {
                    try {
                        Map<String, Object> result = objectMapper.readValue(f, Map.class);
                        score = (int) result.getOrDefault("totalScore", 0);
                        stageScores = (Map<String, Integer>) result.getOrDefault("scoresByStage", new HashMap<>());
                    } catch (Exception ignored) {}
                }
            }
        }

        return new MmseResultResponse(
                userId,
                "COMPLETED",
                "자동 채점 완료: 총점 " + score + "점",
                LocalDateTime.now().toString(),
                stageScores,
                attachments
        );
    }

    public void saveRawMmse(String userId, MmseRawScoreRequest req) {
        CsvUtilMmse.saveRawMmse(userId, req);
    }
}
