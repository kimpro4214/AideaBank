package gift.survey.service;

import gift.survey.dto.DemographicsRequest;
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
public class DemographicsCsvService {

    @Value("${demographics.export-dir:${user.dir}/uploads/demographics/export}")
    private String exportDir;

    public void saveToCsv(DemographicsRequest req) {

        try {
            File dir = new File(exportDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "DEMOGRAPHICS_" + LocalDate.now() + ".csv";
            File csvFile = new File(dir, fileName);

            boolean newFile = csvFile.createNewFile();

            try (FileWriter fw = new FileWriter(csvFile, true)) {

                if (newFile) {
                    fw.write(
                            "user_id,PHC_Age_Cognition,PHC_Sex,PHC_Race,PHC_Education," +
                                    "PTHAND,PTMARRY,PTRTYR,PTTLANG,PTPLANG,PTHOME,created_at\n"
                    );
                }

                fw.write(
                        req.getUserId() + "," +
                                req.getPHC_Age_Cognition() + "," +
                                req.getPHC_Sex() + "," +
                                req.getPHC_Race() + "," +
                                req.getPHC_Education() + "," +
                                req.getPTHAND() + "," +
                                req.getPTMARRY() + "," +
                                req.getPTRTYR() + "," +
                                req.getPTTLANG() + "," +
                                req.getPTPLANG() + "," +
                                req.getPTHOME() + "," +
                                LocalDateTime.now() + "\n"
                );
            }

            log.info("Demographics CSV stored: {}", csvFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Demographics CSV Save Failed: {}", e.getMessage());
        }
    }
}
