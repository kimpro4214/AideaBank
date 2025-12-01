package gift.survey.util;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CsvUtil {

    private static final String FILE_PATH = "src/main/resources/data/survey_status.csv";

    /**
     * 신규 유저 Row 생성
     * 기본 상태: non-start
     */
    public static void addUser(String userId) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {

            writer.write(
                    userId + "," +
                            "non-start," +     // basic_status
                            "non-start," +     // mmse_status
                            "non-start," +     // gds_status
                            "0,0,0,0,0,0,0\n"  // scores
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 상태 업데이트: basic_status, mmse_status, gds_status
     * NEW LOGIC:
     * - allowed values: non-start, in-progress, completed
     */
    public static void updateStatus(String userId, String type, String statusValue) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            List<String> updated = new ArrayList<>();

            for (String line : lines) {

                if (line.startsWith(userId + ",")) {

                    String[] cols = line.split(",");

                    switch (type) {
                        case "basic": cols[1] = statusValue; break;
                        case "mmse": cols[2] = statusValue; break;
                        case "gds": cols[3] = statusValue; break;
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

    /**
     * 점수 저장
     */
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

                } else {
                    updated.add(line);
                }
            }

            Files.write(Paths.get(FILE_PATH), updated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 특정 설문 타입의 상태 조회
     */
    public static String getStatus(String userId, String type) {

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));

            for (String line : lines) {

                if (line.startsWith(userId + ",")) {

                    String[] cols = line.split(",");

                    switch (type) {
                        case "basic": return cols[1];
                        case "mmse": return cols[2];
                        case "gds": return cols[3];
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "non-start";
    }

    /**
     * 전체 row 조회 (Service에서 상태/점수 mapping용)
     */
    public static String[] getStatusRow(String userId) {
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
