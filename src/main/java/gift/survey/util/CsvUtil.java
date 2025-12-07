package gift.survey.util;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CsvUtil {

    private static final String FILE_PATH = "/home/ubuntu/survey_status.csv";

    /**
     * 신규 유저 row 생성 (mmse 12개 + total + 기본 설문 4개 포함)
     */
    public static void addUser(String userId) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {

            writer.write(
                    userId + "," +
                            "non-start," +   // basic_status
                            "non-start," +   // mmse_status
                            "non-start," +   // gds_status
                            "0," +           // gds_score
                            // mmse-1 ~ mmse-12
                            "0,0,0,0,0,0,0,0,0,0,0,0," +
                            "0," +           // mmse_total
                            "0,0,0,0\n"      // 기본 설문 4개
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** 상태 업데이트 (basic / mmse / gds) */
    public static void updateStatus(String userId, String type, String statusValue) {
        try {
            List<String[]> rows = readAll();

            for (String[] row : rows) {
                if (row[0].equals(userId)) {

                    switch (type) {
                        case "basic": row[1] = statusValue; break;
                        case "mmse": row[2] = statusValue; break;
                        case "gds": row[3] = statusValue; break;
                    }
                }
            }

            writeAll(rows);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** GDS 점수 업데이트 (MMSE와 기본설문은 다른 메서드에서 처리) */
    public static void updateScore(String userId, String scoreField, int score) {

        try {
            List<String[]> rows = readAll();

            for (String[] row : rows) {

                if (row[0].equals(userId)) {
                    if (scoreField.equals("gds_score")) {
                        row[4] = String.valueOf(score);
                    }
                }
            }

            writeAll(rows);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** 특정 설문 상태 조회 */
    public static String getStatus(String userId, String type) {
        try {
            List<String[]> rows = readAll();

            for (String[] row : rows) {
                if (row[0].equals(userId)) {

                    switch (type) {
                        case "basic": return row[1];
                        case "mmse": return row[2];
                        case "gds": return row[3];
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "non-start";
    }


    /** CSV row 전체 조회 */
    public static String[] getStatusRow(String userId) {
        try {
            List<String[]> rows = readAll();
            for (String[] row : rows) {
                if (row[0].equals(userId)) return row;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }


    /** MMSE 문항별 점수 저장 */
    public static void updateRawMmse(String userId, Map<String, Integer> scores, int totalScore) {

        List<String[]> rows = readAll();

        for (String[] row : rows) {

            if (row[0].equals(userId)) {

                row[5]  = String.valueOf(scores.getOrDefault("mmse-1", 0));
                row[6]  = String.valueOf(scores.getOrDefault("mmse-2", 0));
                row[7]  = String.valueOf(scores.getOrDefault("mmse-3", 0));
                row[8]  = String.valueOf(scores.getOrDefault("mmse-4", 0));
                row[9]  = String.valueOf(scores.getOrDefault("mmse-5", 0));
                row[10] = String.valueOf(scores.getOrDefault("mmse-6", 0));
                row[11] = String.valueOf(scores.getOrDefault("mmse-7", 0));
                row[12] = String.valueOf(scores.getOrDefault("mmse-8", 0));
                row[13] = String.valueOf(scores.getOrDefault("mmse-9", 0));
                row[14] = String.valueOf(scores.getOrDefault("mmse-10", 0));
                row[15] = String.valueOf(scores.getOrDefault("mmse-11", 0));
                row[16] = String.valueOf(scores.getOrDefault("mmse-12", 0));

                row[17] = String.valueOf(totalScore);
            }
        }

        writeAll(rows);
    }


    /** 🆕 기본 설문 저장 (CSV에 숫자로 저장) */
    public static void updateBasicSurvey(
            String userId,
            int ageCognition,
            int sex,
            int race,
            int education
    ) {
        List<String[]> rows = readAll();

        for (String[] row : rows) {
            if (row[0].equals(userId)) {

                row[18] = String.valueOf(ageCognition);
                row[19] = String.valueOf(sex);
                row[20] = String.valueOf(race);
                row[21] = String.valueOf(education);
            }
        }

        writeAll(rows);
    }


    /** CSV 전체 읽기 */
    private static List<String[]> readAll() {

        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new FileReader(FILE_PATH)) {

            String line;
            BufferedReader br2 = new BufferedReader(br);

            while ((line = br2.readLine()) != null) {
                rows.add(line.split(","));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }


    /** CSV 전체 쓰기 */
    private static void writeAll(List<String[]> rows) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {

            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
