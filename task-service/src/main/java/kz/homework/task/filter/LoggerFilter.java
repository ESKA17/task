package kz.homework.task.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggerFilter extends OncePerRequestFilter {

    private final LogEntryFactory logEntryFactory;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        Map<String, String> requestParts = extractRequestParts(requestWrapper);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            if (requestWrapper.getRequestURI().startsWith("/api/v1")) {
                log.info(logEntryFactory.buildLogEntry(requestWrapper, responseWrapper, requestParts));
            }
            responseWrapper.copyBodyToResponse();
        }
    }

    private Map<String, String> extractRequestParts(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        try {
            if (request.getContentType() != null && request.getContentType().startsWith(MULTIPART_FORM_DATA_VALUE)) {
                for (Part part : request.getParts()) {
                    if (APPLICATION_JSON_VALUE.equals(part.getContentType())) {
                        InputStream inputStream = part.getInputStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        for (int length; (length = inputStream.read(buffer)) != -1; ) {
                            byteArrayOutputStream.write(buffer, 0, length);
                        }
                        result.put(part.getName(), byteArrayOutputStream.toString(request.getCharacterEncoding()));
                    }
                }
            }
        } catch (Exception e) {
            // Note ignoring exceptions
        }
        return result;
    }
}
