package qa_lab.tasklistqalab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullTaskModel {
    UUID id;
    String name;
    String description;
    LocalDate createDate;
    LocalDate updateDate;
    LocalDate deadline;
    TaskStatus status;
    TaskPriority priority;
}
