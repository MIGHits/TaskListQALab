package qa_lab.tasklistqalab.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NiktoSecurityTest {

    @LocalServerPort
    private int port;

    @Test
    public void testNiktoScan() throws Exception {
        Path reportsDir = Paths.get("nikto-reports");
        if (!Files.exists(reportsDir)) {
            Files.createDirectories(reportsDir);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportFile = reportsDir.resolve("nikto_report_" + timestamp + ".txt").toString();

        ProcessBuilder processBuilder = new ProcessBuilder(
            "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe",
            "run", "--rm",
            "hysnsec/nikto",
            "-h", "http://host.docker.internal:" + port,
            "-Format", "txt",
            "-output", "/tmp/nikto_report.txt"
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             FileWriter writer = new FileWriter(reportFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                writer.write(line + "\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Nikto scan failed with exit code: " + exitCode);
        }

        Path reportPath = Paths.get(reportFile);
        if (!Files.exists(reportPath)) {
            System.out.println("Warning: Report file was not created");
            return;
        }

        System.out.println("\nNikto Security Report:");
        System.out.println("Report file: " + reportFile);
        System.out.println("Report content:");
        Files.lines(reportPath).forEach(System.out::println);
    }
} 