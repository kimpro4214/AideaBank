package gift.survey.util;

import gift.survey.dto.MmseRawScoreRequest;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class CsvUtilMmse {

    private static final String EXPORT_DIR = "uploads/mmse/export/";

    public static void saveRawMmse(String userId, MmseRawScoreRequest req) {

        try {
            File dir = new File(EXPORT_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "MMSE_" + LocalDate.now() + ".csv";
            File file = new File(dir, fileName);

            boolean newFile = !file.exists();

            try (FileWriter writer = new FileWriter(file, true)) {

                if (newFile) {
                    writer.write(
                            "user_id,mmse-1,mmse-4,mmse-5,mmse-6,mmse-7," +
                                    "mmse-9,mmse-10,mmse-11,mmse-12,totalScore,submitted_at\n"
                    );
                }

                Map<String, Integer> s = req.getScores();

                String line = String.join(",",
                        userId,
                        String.valueOf(s.getOrDefault("mmse-1", 0)),
                        String.valueOf(s.getOrDefault("mmse-4", 0)),
                        String.valueOf(s.getOrDefault("mmse-5", 0)),
                        String.valueOf(s.getOrDefault("mmse-6", 0)),
                        String.valueOf(s.getOrDefault("mmse-7", 0)),
                        String.valueOf(s.getOrDefault("mmse-9", 0)),
                        String.valueOf(s.getOrDefault("mmse-10", 0)),
                        String.valueOf(s.getOrDefault("mmse-11", 0)),
                        String.valueOf(s.getOrDefault("mmse-12", 0)),
                        String.valueOf(req.getTotalScore()),
                        LocalDateTime.now().toString()
                );

                writer.write(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
