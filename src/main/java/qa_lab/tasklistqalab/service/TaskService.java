package qa_lab.tasklistqalab.service;

import qa_lab.tasklistqalab.dto.EditTaskModel;
import qa_lab.tasklistqalab.dto.FullTaskModel;
import qa_lab.tasklistqalab.dto.ShortTaskModel;
import qa_lab.tasklistqalab.dto.TaskModel;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    UUID createTask(TaskModel taskModel);

    FullTaskModel getTaskById(UUID id);

    List<ShortTaskModel> getAllTasks();

    Void editTask(EditTaskModel taskModel);
}
