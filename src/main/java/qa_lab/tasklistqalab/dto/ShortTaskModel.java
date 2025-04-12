package qa_lab.tasklistqalab.dto;

import lombok.Data;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ShortTaskModel {
    UUID id;
    String name;
    TaskStatus status;
    TaskPriority priority;
    LocalDate deadline;
}
