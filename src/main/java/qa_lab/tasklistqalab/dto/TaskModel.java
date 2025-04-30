package qa_lab.tasklistqalab.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModel {
    @Size(min = 4, max = 100)
    String name;
    @Nullable
    String description;
    @Nullable
    @FutureOrPresent(message = "Дата не может быть раньше текущей")
    LocalDate deadline;
    TaskPriority priority;
}
