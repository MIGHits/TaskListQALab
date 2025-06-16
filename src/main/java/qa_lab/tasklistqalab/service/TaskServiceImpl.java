package qa_lab.tasklistqalab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.SortField;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.exception.BadRequest;
import qa_lab.tasklistqalab.exception.NotFound;
import qa_lab.tasklistqalab.mapper.TaskMapper;
import qa_lab.tasklistqalab.repository.TaskRepository;
import qa_lab.tasklistqalab.specification.TaskSpecification;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UUID createTask(TaskModel task) {
        TaskEntity taskEntity = taskMapper.toEntity(task);
        if (taskEntity.getName().length() <= 3) throw new BadRequest("Минимальная длина наименования 4");
        taskRepository.save(taskEntity);
        return taskEntity.getId();
    }
    

    @Override
    public FullTaskModel getTaskById(UUID id) {
        return taskMapper.toFullTask(taskRepository.findById(id).orElseThrow(() -> new NotFound("Задача с id: " + id + " не найдена")));
    }

    @Override
    public List<ShortTaskModel> getAllTasks(SortField sortField, TaskStatus status, SortDirection sortDirection) {
        // Уязвимый код - SQL-инъекция через конкатенацию строк
        String statusFilter = "";
        if (status != null) {
            statusFilter = " AND status = '" + status.toString() + "'";
        }

        String sortClause = "";
        if (sortField != null) {
            sortClause = " ORDER BY " + sortField.toString() + " " + sortDirection.toString();
        }

        String sql = "SELECT * FROM tasks WHERE 1=1" + statusFilter + sortClause;
        
        List<TaskEntity> tasks = jdbcTemplate.query(sql, (rs, rowNum) -> {
            TaskEntity task = new TaskEntity();
            task.setId(UUID.fromString(rs.getString("id")));
            task.setName(rs.getString("name"));
            task.setDescription(rs.getString("description"));
            task.setStatus(TaskStatus.valueOf(rs.getString("status")));
            return task;
        });

        return taskMapper.toShortTask(tasks);
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
        if (taskEntity.getDeadline() != null) {
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
        } else {
            if (taskEntity.getStatus().equals(TaskStatus.COMPLETED)) {
                taskEntity.setStatus(TaskStatus.ACTIVE);
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
