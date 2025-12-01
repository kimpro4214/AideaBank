package gift.survey.dto;

public class GdsResponse {

    private int score;
    private String level;
    private boolean depressionRisk;

    public GdsResponse(int score, String level, boolean depressionRisk) {
        this.score = score;
        this.level = level;
        this.depressionRisk = depressionRisk;
    }

    public int getScore() {
        return score;
    }

    public String getLevel() {
        return level;
    }

    public boolean isDepressionRisk() {
        return depressionRisk;
    }
}
