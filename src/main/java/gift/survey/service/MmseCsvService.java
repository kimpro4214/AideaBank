package gift.survey.service;

import gift.survey.dto.MmseScoreRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class MmseCsvService {

    @Value("${mmse.export-dir:${user.dir}/uploads/mmse/export}")
    private String exportDir;

    public void saveToCsv(MmseScoreRequest req) {

        try {
            // export 폴더 생성
            File dir = new File(exportDir);
            if (!dir.exists()) dir.mkdirs();

            // 날짜별 파일
            String fileName = "MMSE_" + LocalDate.now() + ".csv";
            File csvFile = new File(dir, fileName);

            boolean newFile = csvFile.createNewFile();

            try (FileWriter fw = new FileWriter(csvFile, true)) {

                // 첫 생성 시 헤더 추가
                if (newFile) {
                    fw.write("user_id,mmse-1,mmse-4,mmse-5,mmse-6,mmse-7,mmse-9,mmse-10,mmse-11,mmse-12,total,created_at\n");
                }

                // 값 채우기
                fw.write(
                        req.getUserId() + "," +
                                req.getScores().getOrDefault("mmse-1", 0) + "," +
                                req.getScores().getOrDefault("mmse-4", 0) + "," +
                                req.getScores().getOrDefault("mmse-5", 0) + "," +
                                req.getScores().getOrDefault("mmse-6", 0) + "," +
                                req.getScores().getOrDefault("mmse-7", 0) + "," +
                                req.getScores().getOrDefault("mmse-9", 0) + "," +
                                req.getScores().getOrDefault("mmse-10", 0) + "," +
                                req.getScores().getOrDefault("mmse-11", 0) + "," +
                                req.getScores().getOrDefault("mmse-12", 0) + "," +
                                req.getTotalScore() + "," +
                                LocalDateTime.now() + "\n"
                );
            }

            log.info("CSV 저장 완료: {}", csvFile.getAbsolutePath());

        } catch (IOException e) {
            log.error("CSV 저장 실패: {}", e.getMessage());
        }
    }
}
