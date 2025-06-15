package qa_lab.tasklistqalab.config;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SqlInjectionValidator {
    private static final Pattern SQL_PATTERN = Pattern.compile(
        "(?i)(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER|CREATE|TRUNCATE|EXEC|DECLARE|WAITFOR|DELAY|SLEEP|BENCHMARK|LOAD_FILE|OUTFILE|DUMPFILE|INTO\\s+OUTFILE|INTO\\s+DUMPFILE|\\s+OR\\s+\\d+=\\d+|\\s+OR\\s+'\\w+'='\\w+'|\\s+OR\\s+\\d+=\\d+--|\\s+OR\\s+'\\w+'='\\w+'--)"
    );

    public boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }
        return !SQL_PATTERN.matcher(input).find();
    }

    public void validate(String input) {
        if (!isValid(input)) {
            throw new IllegalArgumentException("Potential SQL injection detected");
        }
    }
} 