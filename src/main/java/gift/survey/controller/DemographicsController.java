package gift.survey.controller;

import gift.survey.dto.DemographicsRequest;
import gift.survey.service.DemographicsCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/demographics")
@RequiredArgsConstructor
public class DemographicsController {

    private final DemographicsCsvService csvService;

    @PostMapping("/submit")
    public Map<String, Object> submit(@RequestBody DemographicsRequest req) {

        csvService.saveToCsv(req);

        return Map.of(
                "status", "OK",
                "message", "Demographic data received and stored to CSV",
                "userId", req.getUserId()
        );
    }
}
