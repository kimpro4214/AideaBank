package gift.survey.util;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    private static final String FILE_PATH = "src/main/resources/data/survey_status.csv";

    // 신규 유저 Row 생성
    public static void addUser(String userId) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(
                    userId + ",false,false,false,0,0,0,0,0,0,0\n"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // basic/mmse/gds 완료 여부 업데이트
    public static void updateStatus(String userId, String fieldName, boolean value) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(userId + ",")) {
                    String[] cols = line.split(",");

                    String val = Boolean.toString(value);

                    switch (fieldName) {
                        case "basic": cols[1] = val; break;
                        case "mmse": cols[2] = val; break;
                        case "gds": cols[3] = val; break;
                    }
                    updated.add(String.join(",", cols));
                } else {
                    updated.add(line);
                }
            }
            Files.write(Paths.get(FILE_PATH), updated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 점수 업데이트 (GDS, MMSE 각 항목)
    public static void updateScore(String userId, String scoreField, int score) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            List<String> updated = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(userId + ",")) {

                    String[] cols = line.split(",");

                    switch (scoreField) {
                        case "gds_score": cols[4] = String.valueOf(score); break;
                        case "mmse_time_score": cols[5] = String.valueOf(score); break;
                        case "mmse_registration_score": cols[6] = String.valueOf(score); break;
                        case "mmse_recall_score": cols[7] = String.valueOf(score); break;
                        case "mmse_attention_score": cols[8] = String.valueOf(score); break;
                        case "mmse_language_score": cols[9] = String.valueOf(score); break;
                        case "mmse_copy_score": cols[10] = String.valueOf(score); break;
                    }
                    updated.add(String.join(",", cols));

                } else updated.add(line);
            }

            Files.write(Paths.get(FILE_PATH), updated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 상태 조회
    public static String[] getStatus(String userId) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                if (line.startsWith(userId + ",")) {
                    return line.split(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
