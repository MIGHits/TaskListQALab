package qa_lab.tasklistqalab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qa_lab.tasklistqalab.dto.EditTaskModel;
import qa_lab.tasklistqalab.dto.FullTaskModel;
import qa_lab.tasklistqalab.dto.ShortTaskModel;
import qa_lab.tasklistqalab.dto.TaskModel;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.mapper.TaskMapper;
import qa_lab.tasklistqalab.repository.TaskRepository;

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
    public Void editTask(EditTaskModel taskModel) {
        taskRepository.findById(taskModel.getId()).orElse(null);
        taskRepository.save(taskMapper.);
    }
}
