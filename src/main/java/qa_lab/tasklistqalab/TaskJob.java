package qa_lab.tasklistqalab;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TaskJob {
    @Scheduled(cron = "0 */1 * * * *")
    public void taskJob() throws InterruptedException {

    }
}
