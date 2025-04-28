package qa_lab.tasklistqalab.service;

import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.SortField;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    UUID createTask(TaskModel taskModel);

    FullTaskModel getTaskById(UUID id);

    List<ShortTaskModel> getAllTasks(SortField sortField, TaskStatus status, SortDirection sortDirection);

    ResponseModel editTask(EditTaskModel taskModel);

    Void changeTaskStatus(UUID id);

    ResponseModel deleteTask(UUID id);
}
