package qa_lab.tasklistqalab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.SortField;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.exception.NotFound;
import qa_lab.tasklistqalab.mapper.TaskMapper;
import qa_lab.tasklistqalab.repository.TaskRepository;
import qa_lab.tasklistqalab.specification.TaskSpecification;

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
        return taskMapper.toFullTask(taskRepository.findById(id).orElseThrow(() -> new NotFound("Задача с id: " + id + " не найдена")));
    }

    @Override
    public List<ShortTaskModel> getAllTasks(SortField sortField, TaskStatus status, SortDirection sortDirection) {
        Specification<TaskEntity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and(TaskSpecification.filterByStatus(status));
        }

        if (sortField != null) {
            switch (sortField) {
                case PRIORITY -> spec = spec.and(TaskSpecification.sortByPriority(sortDirection));
                case CREATION_DATE -> spec = spec.and(TaskSpecification.sortByCreateTime(sortDirection));
            }
        }

        return taskMapper.toShortTask(taskRepository.findAll(spec));
    }

    @Override
    public ResponseModel editTask(EditTaskModel taskModel) {
        taskRepository.findById(taskModel.getId()).orElseThrow(() -> new NotFound("Задача с id: " + taskModel.getId() + " не найдена"));
        taskRepository.save(taskMapper.fromEdit(taskModel));
        return ResponseModel.builder()
                .status("success")
                .message("Задача успешно обновлена")
                .build();
    }

    @Override
    public Void changeTaskStatus(UUID id) {
        TaskEntity taskEntity = taskRepository.findById(id).orElseThrow(() -> new NotFound("Задача с id: " + id + " не найдена"));

        if (taskEntity.getStatus().equals(TaskStatus.COMPLETED) ||
                taskEntity.getStatus().equals(TaskStatus.LATE)) {
            if (taskEntity.getDeadline().isBefore(LocalDate.now()) ||
                    taskEntity.getDeadline().isEqual(LocalDate.now())) {
                taskEntity.setStatus(TaskStatus.OVERDUE);
            } else {
                taskEntity.setStatus(TaskStatus.ACTIVE);
            }
        } else {
            if (taskEntity.getDeadline().isBefore(LocalDate.now()) ||
                    taskEntity.getDeadline().isEqual(LocalDate.now())) {
                taskEntity.setStatus(TaskStatus.LATE);
            } else {
                taskEntity.setStatus(TaskStatus.COMPLETED);
            }
        }
        taskRepository.save(taskEntity);
        return null;
    }

    @Override
    public ResponseModel deleteTask(UUID id) {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new NotFound("Задача с id: " + id + " не найдена"));
        taskRepository.delete(task);
        return ResponseModel.builder().message("Задача успешно удалена").status("success").build();

    }
}
