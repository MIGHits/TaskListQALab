package qa_lab.tasklistqalab;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import qa_lab.tasklistqalab.service.TaskService;

@SpringBootApplication
@EnableScheduling
public class TaskListQaLabApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskListQaLabApplication.class, args);
    }
}
