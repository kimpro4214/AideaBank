package gift.survey.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Bean
    public Client genAIClient() {
        return new Client(); // 최초 1회만 생성됨
    }
}

