package kz.homework.task.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.LINE_SEPARATOR;

@Component
public class LogEntryFactory {

    private static final String MASKING_PATTERN = "(\"fileContents\")\\s?:\\s?\"\\S*?\"";
    private static final String MASK_TEMPLATE = "$1 : \"***\"";

    public String buildLogEntry(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, Map<String, String> requestParts) {
        return buildRequestLogEntry(request, requestParts) + buildResponseLogEntry(response);
    }

    private String buildRequestLogEntry(ContentCachingRequestWrapper request, Map<String, String> requestParts) {
        return LINE_SEPARATOR + "REQUEST: " + LINE_SEPARATOR +
                request.getMethod() + " " + request.getRequestURI() + LINE_SEPARATOR +
                "parameters: " + extractParameters(request) + LINE_SEPARATOR +
                "parts: " + requestParts + LINE_SEPARATOR +
                "headers: " + LINE_SEPARATOR +
                extractRequestHeaders(request) + LINE_SEPARATOR +
                "body: " + LINE_SEPARATOR +
                maskBody(request.getContentAsByteArray()) + LINE_SEPARATOR;
    }

    private String buildResponseLogEntry(ContentCachingResponseWrapper response) {
        return "RESPONSE: " + response.getStatus() + LINE_SEPARATOR +
                "headers: " + LINE_SEPARATOR +
                extractResponseHeaders(response) + LINE_SEPARATOR +
                "body: " + LINE_SEPARATOR +
                maskBody(response.getContentAsByteArray()) + LINE_SEPARATOR;
    }

    private Map<String, String> extractRequestHeaders(ContentCachingRequestWrapper request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
                headers.put(name, "***");
            } else {
                headers.put(name, request.getHeader(name));
            }
        }
        return headers;
    }

    private Map<String, String> extractResponseHeaders(ContentCachingResponseWrapper response) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String name : response.getHeaderNames()) {
            headers.put(name, response.getHeader(name));
        }
        return headers;
    }

    private Map<String, String> extractParameters(ContentCachingRequestWrapper request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        return parameterMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Arrays.toString(entry.getValue()).replaceAll(MASKING_PATTERN, MASK_TEMPLATE)
                ));
    }

    private String maskBody(byte[] body) {
        return new String(body).replaceAll(MASKING_PATTERN, MASK_TEMPLATE);
    }
}
