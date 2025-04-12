package qa_lab.tasklistqalab.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;

import java.time.LocalDate;

@Data
public class TaskModel {
    String name;
    @Nullable
    String description;
    @Nullable
    LocalDate deadline;
    TaskPriority priority;
}
