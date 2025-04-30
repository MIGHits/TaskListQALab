package qa_lab.tasklistqalab.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import qa_lab.tasklistqalab.dto.EditTaskModel;
import qa_lab.tasklistqalab.dto.FullTaskModel;
import qa_lab.tasklistqalab.dto.ShortTaskModel;
import qa_lab.tasklistqalab.dto.TaskModel;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {
    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void toEntity() {
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Test Task");
        taskModel.setDescription("Test Description");
        taskModel.setDeadline(LocalDate.of(2025, 12, 31));

        TaskEntity taskEntity = taskMapper.toEntity(taskModel);

        assertEquals("Test Task", taskEntity.getName());
        assertEquals("Test Description", taskEntity.getDescription());
        assertEquals(TaskPriority.MEDIUM, taskEntity.getPriority());
        assertEquals(LocalDate.of(2025, 12, 31), taskEntity.getDeadline());
        assertEquals(TaskStatus.ACTIVE, taskEntity.getStatus());
    }

    @Test
    void toEntityDeadline() {
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Test !before 01.05.2025");
        taskModel.setDescription("Test Description");

        TaskEntity taskEntity = taskMapper.toEntity(taskModel);

        assertEquals(LocalDate.of(2025, 5, 1), taskEntity.getDeadline());
    }

    @Test
    void toEntityPriorityStartBoundarySuccess() {
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Test !1");
        taskModel.setDescription("Test Description");

        TaskEntity taskEntity = taskMapper.toEntity(taskModel);

        assertEquals(TaskPriority.CRITICAL, taskEntity.getPriority());
    }

    @Test
    void toEntityPriorityStartBoundaryNegative() {
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Test !0");
        taskModel.setDescription("Test Description");

        TaskEntity taskEntity = taskMapper.toEntity(taskModel);

        assertEquals(TaskPriority.MEDIUM, taskEntity.getPriority());
        assertEquals("Test !0", taskEntity.getName());
    }

    @Test
    void toEntityPriorityMedium() {
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Test !3");
        taskModel.setDescription("Test Description");

        TaskEntity taskEntity = taskMapper.toEntity(taskModel);

        assertEquals(TaskPriority.MEDIUM, taskEntity.getPriority());
    }

    @Test
    void toEntityPriorityEndBoundaryNegative() {
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Test !5");
        taskModel.setDescription("Test Description");

        TaskEntity taskEntity = taskMapper.toEntity(taskModel);

        assertEquals(TaskPriority.MEDIUM, taskEntity.getPriority());
        assertEquals("Test !5", taskEntity.getName());
    }

    @Test
    void processTaskNameAndPriority_ExtractsPriorityAndDeadline() {
        TaskModel model = new TaskModel();
        model.setName("Important !1 !before 30.04.2025");
        model.setDescription("Should extract priority and deadline");

        TaskEntity entity = taskMapper.toEntity(model);

        assertEquals("Important", entity.getName());
        assertEquals(TaskPriority.CRITICAL, entity.getPriority());
        assertEquals(LocalDate.of(2025, 4, 30), entity.getDeadline());
    }

    @Test
    void toFullTaskSuccess() {
        TaskEntity entity = new TaskEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Test Task");
        entity.setDescription("Full description");
        entity.setCreationDate(LocalDate.now());
        entity.setDeadline(LocalDate.of(2025, 6, 1));
        entity.setPriority(TaskPriority.HIGH);
        entity.setStatus(TaskStatus.ACTIVE);

        FullTaskModel model = taskMapper.toFullTask(entity);

        assertEquals(entity.getName(), model.getName());
        assertEquals(entity.getDescription(), model.getDescription());
        assertEquals(entity.getCreationDate(), model.getCreateDate());
        assertEquals(entity.getDeadline(), model.getDeadline());
        assertEquals(entity.getPriority(), model.getPriority());
        assertEquals(entity.getStatus(), model.getStatus());
    }

    @Test
    void toShortTask() {
        TaskEntity entity1 = new TaskEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setName("Task 1");
        entity1.setPriority(TaskPriority.LOW);
        entity1.setStatus(TaskStatus.COMPLETED);

        TaskEntity entity2 = new TaskEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setName("Task 2");
        entity2.setPriority(TaskPriority.CRITICAL);
        entity2.setStatus(TaskStatus.ACTIVE);

        List<TaskEntity> entities = List.of(entity1, entity2);
        List<ShortTaskModel> result = taskMapper.toShortTask(entities);

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getName());
        assertEquals("Task 2", result.get(1).getName());
    }

    @Test
    void fromEditSuccess() {
        EditTaskModel editModel = new EditTaskModel();
        editModel.setId(UUID.randomUUID());
        editModel.setName("Edited Task");
        editModel.setDescription("Edited Description");
        editModel.setDeadline(LocalDate.of(2025, 7, 1));
        editModel.setPriority(TaskPriority.HIGH);

        TaskEntity entity = taskMapper.fromEdit(editModel);

        assertEquals("Edited Task", entity.getName());
        assertEquals("Edited Description", entity.getDescription());
        assertEquals(LocalDate.of(2025, 7, 1), entity.getDeadline());
        assertEquals(TaskPriority.HIGH, entity.getPriority());
        assertEquals(TaskStatus.ACTIVE, entity.getStatus());
    }
}