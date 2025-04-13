package qa_lab.tasklistqalab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.mapper.TaskMapper;
import qa_lab.tasklistqalab.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public UUID createTask(TaskModel task) {
        TaskEntity taskEntity = taskMapper.toEntity(task);
        taskRepository.save(taskEntity);
        return taskEntity.getId();
    }

    @Override
    public FullTaskModel getTaskById(UUID id) {
        return taskMapper.toFullTask(taskRepository.findById(id).orElse(null));
    }

    @Override
    public List<ShortTaskModel> getAllTasks() {
        return taskMapper.toShortTask(taskRepository.findAll());
    }

    @Override
    public ResponseModel editTask(EditTaskModel taskModel) {
        taskRepository.findById(taskModel.getId()).orElseThrow();
        taskRepository.save(taskMapper.fromEdit(taskModel));
        return ResponseModel.builder()
                .status("success")
                .message("Задача успешно обновлена")
                .build();
    }

    @Override
    public Void changeTaskStatus(UUID id) {
        TaskEntity taskEntity = taskRepository.findById(id).orElseThrow();

        if (taskEntity.getStatus().equals(TaskStatus.COMPLETED)) {
            if (taskEntity.getDeadline().isBefore(LocalDate.now()) ||
                    taskEntity.getDeadline().isEqual(LocalDate.now())) {
                taskEntity.setStatus(TaskStatus.OVERDUE);
            } else {
                taskEntity.setStatus(TaskStatus.ACTIVE);
            }
        } else {
            if (taskEntity.getDeadline().isBefore(LocalDate.now())) {
                taskEntity.setStatus(TaskStatus.LATE);
            } else {
                taskEntity.setStatus(TaskStatus.COMPLETED);
            }
        }
        taskRepository.save(taskEntity);
        return null;
    }
}
