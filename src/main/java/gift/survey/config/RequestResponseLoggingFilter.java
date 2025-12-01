package gift.survey.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(res);

        long startTime = System.currentTimeMillis();

        filterChain.doFilter(request, response); // 실제 컨트롤러 실행

        long duration = System.currentTimeMillis() - startTime;

        // Request Body
        String requestBody = new String(request.getContentAsByteArray());

        // Response Body
        String responseBody = new String(response.getContentAsByteArray());

        System.out.println("\n===== Request Log =====");
        System.out.println(request.getMethod() + " " + request.getRequestURI());
        System.out.println("Headers: ");
        request.getHeaderNames().asIterator().forEachRemaining(h ->
                System.out.println("  " + h + ": " + request.getHeader(h))
        );
        System.out.println("Body: " + requestBody);

        System.out.println("\n===== Response Log =====");
        System.out.println("Status: " + response.getStatus());
        System.out.println("Body: " + responseBody);
        System.out.println("Time: " + duration + "ms");
        System.out.println("========================\n");

        response.copyBodyToResponse();
    }
}
