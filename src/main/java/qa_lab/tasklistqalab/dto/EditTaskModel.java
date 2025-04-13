package qa_lab.tasklistqalab.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EditTaskModel {
    UUID id;
    @Size(min = 4, max = 100)
    String name;
    @Nullable
    String description;
    @Nullable
    @FutureOrPresent(message = "Дата не может быть раньше текущей")
    LocalDate deadline;
    TaskPriority priority;
}
