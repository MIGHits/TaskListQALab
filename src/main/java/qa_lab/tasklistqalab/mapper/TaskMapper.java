package qa_lab.tasklistqalab.mapper;

import org.mapstruct.*;
import qa_lab.tasklistqalab.dto.EditTaskModel;
import qa_lab.tasklistqalab.dto.FullTaskModel;
import qa_lab.tasklistqalab.dto.ShortTaskModel;
import qa_lab.tasklistqalab.dto.TaskModel;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "status", expression = "java(qa_lab.tasklistqalab.entity.enum_model.TaskStatus.ACTIVE)")
    TaskEntity toEntity(TaskModel taskModel);

    FullTaskModel toFullTask(TaskEntity taskEntity);

    List<ShortTaskModel> toShortTask(List<TaskEntity> taskEntity);

    @Mapping(target = "status", expression = "java(qa_lab.tasklistqalab.entity.enum_model.TaskStatus.ACTIVE)")
    TaskEntity fromEdit (EditTaskModel fullTaskModel);

    @AfterMapping
    default void processTaskNameAndPriority(
            @MappingTarget TaskEntity taskEntity,
            TaskModel taskModel
    ) {
        String taskName = taskModel.getName();

        if (taskModel.getPriority() == null) {
            TaskPriority priority = extractPriorityFromName(taskName);
            if (priority != null) {
                taskEntity.setPriority(priority);
                taskName = removePriorityMacro(taskName);
            } else {
                taskEntity.setPriority(TaskPriority.MEDIUM);
            }
        }

        if (taskModel.getDeadline() == null) {
            LocalDate deadline = extractDeadlineFromName(taskName);
            if (deadline != null) {
                taskEntity.setDeadline(deadline);
                taskName = removeDeadlineMacro(taskName);
            }
        }
        taskName = removePriorityMacro(taskName);
        taskName = removeDeadlineMacro(taskName);

        taskEntity.setName(taskName);
    }

    private TaskPriority extractPriorityFromName(String name) {
        if (name.contains("!1")) return TaskPriority.CRITICAL;
        if (name.contains("!2")) return TaskPriority.HIGH;
        if (name.contains("!3")) return TaskPriority.MEDIUM;
        if (name.contains("!4")) return TaskPriority.LOW;
        return null;
    }

    private String removePriorityMacro(String name) {
        return name.replaceAll("![1-4]", "").trim();
    }

    private LocalDate extractDeadlineFromName(String name) {
        Pattern pattern = Pattern.compile("!before (\\d{2}[.-]\\d{2}[.-]\\d{4})");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String dateStr = matcher.group(1).replace('-', '.');
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateStr, formatter);
        }
        return null;
    }

    private String removeDeadlineMacro(String name) {
        return name.replaceAll("!before \\d{2}[.-]\\d{2}[.-]\\d{4}", "").trim();
    }
}
