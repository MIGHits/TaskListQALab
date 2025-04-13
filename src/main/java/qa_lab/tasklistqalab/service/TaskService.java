package qa_lab.tasklistqalab.service;

import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    UUID createTask(TaskModel taskModel);

    FullTaskModel getTaskById(UUID id);

    List<ShortTaskModel> getAllTasks();

    ResponseModel editTask(EditTaskModel taskModel);

    Void changeTaskStatus(UUID id);
}
