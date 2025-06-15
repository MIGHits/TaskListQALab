package qa_lab.tasklistqalab;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;

import static java.lang.System.in;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class TaskJob {
    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 */1 * * * *")
    public void taskJob() throws InterruptedException {
        taskRepository.findAll().forEach(task -> {
            if (task.getDeadline() != null && 
                LocalDate.now().isEqual(task.getDeadline()) &&
                !List.of(TaskStatus.LATE, TaskStatus.COMPLETED).contains(task.getStatus())) {
                task.setStatus(TaskStatus.OVERDUE);
            } else if (task.getStatus() != TaskStatus.COMPLETED) {
                task.setStatus(TaskStatus.ACTIVE);
            }
            taskRepository.save(task);
        });
    }
}
