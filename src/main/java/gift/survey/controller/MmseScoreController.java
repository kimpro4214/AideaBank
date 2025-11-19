package gift.survey.controller;

import gift.survey.dto.MmseScoreRequest;
import gift.survey.service.MmseCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mmse")
@RequiredArgsConstructor
public class MmseScoreController {

    private final MmseCsvService csvService;

    @PostMapping("/score")
    public Map<String, Object> submitScore(@RequestBody MmseScoreRequest req) {

        csvService.saveToCsv(req);

        return Map.of(
                "status", "OK",
                "message", "MMSE score received and stored to CSV",
                "userId", req.getUserId()
        );
    }
}
