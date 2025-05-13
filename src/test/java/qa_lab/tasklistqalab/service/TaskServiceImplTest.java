package qa_lab.tasklistqalab.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.SortField;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;
import qa_lab.tasklistqalab.exception.BadRequest;
import qa_lab.tasklistqalab.mapper.TaskMapper;
import qa_lab.tasklistqalab.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.data.jpa.domain.Specification;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.exception.NotFound;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void createTaskSuccess() {
        TaskModel taskModel = new TaskModel();
        TaskEntity taskEntity = new TaskEntity();
        UUID expectedId = UUID.randomUUID();

        when(taskMapper.toEntity(taskModel)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenAnswer(invocation -> {
            TaskEntity saved = invocation.getArgument(0);
            saved.setId(expectedId);
            return saved;
        });


        UUID result = taskService.createTask(taskModel);

        assertEquals(expectedId, result);
        verify(taskMapper).toEntity(taskModel);
        verify(taskRepository).save(taskEntity);
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testInvalidNameTooShort() {
        TaskModel model = new TaskModel("123", "desc", LocalDate.now().plusDays(1), TaskPriority.MEDIUM);

        Set<ConstraintViolation<TaskModel>> violations = validator.validate(model);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testInvalidPastDeadline() {
        TaskModel model = new TaskModel("Valid Name", "desc", LocalDate.now().minusDays(1), TaskPriority.MEDIUM);

        Set<ConstraintViolation<TaskModel>> violations = validator.validate(model);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deadline")));
    }

    @Test
    void getTaskByIdSuccess() {

        UUID taskId = UUID.randomUUID();
        TaskEntity taskEntity = new TaskEntity();
        FullTaskModel expectedModel = new FullTaskModel();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskMapper.toFullTask(taskEntity)).thenReturn(expectedModel);

        FullTaskModel result = taskService.getTaskById(taskId);

        assertEquals(expectedModel, result);
        verify(taskRepository).findById(taskId);
        verify(taskMapper).toFullTask(taskEntity);
    }

    @Test
    void getTaskByIdNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> taskService.getTaskById(taskId));
        verify(taskRepository).findById(taskId);
    }

    @ParameterizedTest
    @MethodSource("provideSortAndStatusParams")
    void getAllTasks(SortField sortField, TaskStatus status, SortDirection sortDirection) {
        List<TaskEntity> entities = List.of(new TaskEntity(), new TaskEntity());
        List<ShortTaskModel> expectedModels = List.of(new ShortTaskModel(), new ShortTaskModel());

        when(taskRepository.findAll(any(Specification.class))).thenReturn(entities);
        when(taskMapper.toShortTask(entities)).thenReturn(expectedModels);

        List<ShortTaskModel> result = taskService.getAllTasks(sortField, status, sortDirection);

        assertEquals(expectedModels, result);
        verify(taskRepository).findAll(any(Specification.class));
        verify(taskMapper).toShortTask(entities);
    }

    static Stream<Arguments> provideSortAndStatusParams() {
        return Stream.of(
                Arguments.of(SortField.PRIORITY, TaskStatus.OVERDUE, SortDirection.ASC),
                Arguments.of(SortField.CREATION_DATE, TaskStatus.ACTIVE, SortDirection.DESC),
                Arguments.of(null, TaskStatus.COMPLETED, null),
                Arguments.of(SortField.PRIORITY, null, SortDirection.DESC),
                Arguments.of(null, null, null)
        );
    }

    @Test
    void getAllTasksWithStatusAndSortShouldReturnExpectedList() {
        List<TaskEntity> taskEntities = List.of(
                new TaskEntity(
                        UUID.randomUUID(),
                        "name1",
                        "desc",
                        LocalDate.now(),
                        TaskStatus.ACTIVE,
                        LocalDate.now(),
                        TaskPriority.CRITICAL,
                        LocalDate.now()),
                new TaskEntity(
                        UUID.randomUUID(),
                        "name2",
                        "desc",
                        LocalDate.now(),
                        TaskStatus.ACTIVE,
                        LocalDate.now(),
                        TaskPriority.MEDIUM,
                        LocalDate.now()));
        List<ShortTaskModel> expectedModels = List.of(new ShortTaskModel(
                UUID.randomUUID(),
                "name1",
                TaskStatus.ACTIVE,
                TaskPriority.CRITICAL,
                LocalDate.now()));

        when(taskRepository.findAll(any(Specification.class))).thenReturn(taskEntities);
        when(taskMapper.toShortTask(taskEntities)).thenReturn(expectedModels);

        List<ShortTaskModel> result = taskService.getAllTasks(SortField.CREATION_DATE, TaskStatus.ACTIVE, SortDirection.ASC);

        assertEquals(expectedModels, result);
        verify(taskRepository).findAll(any(Specification.class));
    }

    @Test
    void editTaskSuccess() {

        UUID taskId = UUID.randomUUID();
        EditTaskModel editModel = new EditTaskModel();
        editModel.setId(taskId);
        TaskEntity existingEntity = new TaskEntity();
        TaskEntity updatedEntity = new TaskEntity();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingEntity));
        when(taskMapper.fromEdit(editModel)).thenReturn(updatedEntity);
        when(taskRepository.save(updatedEntity)).thenReturn(updatedEntity);

        ResponseModel result = taskService.editTask(editModel);

        assertEquals("success", result.getStatus());
        assertEquals("Задача успешно обновлена", result.getMessage());
        verify(taskRepository).findById(taskId);
        verify(taskMapper).fromEdit(editModel);
        verify(taskRepository).save(updatedEntity);
    }

    @Test
    void editTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        EditTaskModel editModel = new EditTaskModel();
        editModel.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> taskService.editTask(editModel));
        verify(taskRepository).findById(taskId);
    }

    @ParameterizedTest
    @MethodSource("provideTaskStatusChangeCases")
    void changeTaskStatus(
            TaskStatus initialStatus,
            LocalDate deadline,
            TaskStatus expectedStatus) {

        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity();
        task.setStatus(initialStatus);
        task.setDeadline(deadline);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.changeTaskStatus(taskId);

        assertEquals(expectedStatus, task.getStatus());
        verify(taskRepository).save(task);
    }

    private static Stream<Arguments> provideTaskStatusChangeCases() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        return Stream.of(
                Arguments.of(TaskStatus.ACTIVE, tomorrow, TaskStatus.COMPLETED),

                Arguments.of(TaskStatus.ACTIVE, yesterday, TaskStatus.LATE),

                Arguments.of(TaskStatus.COMPLETED, tomorrow, TaskStatus.ACTIVE),

                Arguments.of(TaskStatus.COMPLETED, yesterday, TaskStatus.OVERDUE),

                Arguments.of(TaskStatus.ACTIVE, null, TaskStatus.COMPLETED),

                Arguments.of(TaskStatus.COMPLETED, null, TaskStatus.ACTIVE)
        );
    }

    @Test
    void deleteTaskSuccess() {

        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ResponseModel result = taskService.deleteTask(taskId);

        assertEquals("success", result.getStatus());
        assertEquals("Задача успешно удалена", result.getMessage());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity();
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> taskService.deleteTask(taskId));
        verify(taskRepository).findById(taskId);
    }
}