package qa_lab.tasklistqalab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.SortField;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.exception.NotFound;
import qa_lab.tasklistqalab.service.TaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public TaskService taskService() {
            return Mockito.mock(TaskService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper;
        }

    }

    @Test
    void createTask_Success() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();

        when(taskService.createTask(taskModel)).thenReturn(taskId);

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(taskId.toString()));
    }

    @Test
    void createTask_InvalidJson_BadRequest() throws Exception {
        String invalidJson = "{ \"name\": \"task1\" ";

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_NameValidationFailure() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel(
                "abc",
                "desc",
                LocalDate.now().plusDays(1),
                TaskPriority.MEDIUM);

        when(taskService.createTask(taskModel)).thenReturn(taskId);

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskModel)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: Поле 'name': размер должен находиться в диапазоне от 4 до 100"));
    }

    @Test
    void createTask_DeadlineValidationFailure() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel(
                "abcd",
                "desc",
                LocalDate.now().minusDays(1),
                TaskPriority.MEDIUM);

        when(taskService.createTask(taskModel)).thenReturn(taskId);

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskModel)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: Поле 'deadline': Дата не может быть раньше текущей"));
    }

    @Test
    void createTask_Failure() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel(
                "abc",
                "desc",
                LocalDate.now().minusDays(1),
                TaskPriority.MEDIUM);

        when(taskService.createTask(taskModel)).thenReturn(taskId);

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskModel)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: Поле 'deadline': Дата не может быть раньше текущей; Поле 'name': размер должен находиться в диапазоне от 4 до 100"));
    }

    @Test
    void getTaskById_Success() throws Exception {
        UUID id = UUID.randomUUID();
        FullTaskModel task = new FullTaskModel();
        task.setId(id);

        when(taskService.getTaskById(id)).thenReturn(task);

        mockMvc.perform(get("/api/task/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getTaskById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        FullTaskModel task = new FullTaskModel();
        task.setId(id);

        when(taskService.getTaskById(id)).thenThrow(new NotFound("Задача с id: " + id + " не найдена"));

        mockMvc.perform(get("/api/task/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_Success() throws Exception {
        List<ShortTaskModel> tasks = List.of(new ShortTaskModel());
        when(taskService.getAllTasks(any(), any(), any())).thenReturn(tasks);

        mockMvc.perform(get("/api/task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAllTasks_StatusFilter() throws Exception {
        List<ShortTaskModel> tasks = List.of(new ShortTaskModel(
                        UUID.randomUUID(),
                        "name1",
                        TaskStatus.ACTIVE,
                        TaskPriority.CRITICAL,
                        LocalDate.now()),
                new ShortTaskModel(
                        UUID.randomUUID(),
                        "name2",
                        TaskStatus.COMPLETED,
                        TaskPriority.CRITICAL,
                        LocalDate.now()));
        when(taskService.getAllTasks(
                eq(SortField.CREATION_DATE),
                eq(TaskStatus.COMPLETED),
                eq(SortDirection.ASC)))
                .thenReturn(List.of(tasks.get(1)));

        mockMvc.perform(get("/api/task").param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(tasks.get(1).getId().toString()));
    }

    @Test
    void updateTask_Success() throws Exception {
        EditTaskModel model = new EditTaskModel();
        ResponseModel response = new ResponseModel("Success", "Task Updated Successfully");

        when(taskService.editTask(any())).thenReturn(response);

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task Updated Successfully"));
    }

    @Test
    void updateTask_BadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        EditTaskModel model = new EditTaskModel();
        model.setName("nam");
        model.setId(id);

        when(taskService.editTask(model)).thenReturn(new ResponseModel("Success", "Task updated successfully"));

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        EditTaskModel model = new EditTaskModel();
        model.setId(id);
        model.setName("name1");
        model.setDescription("desc1");
        model.setPriority(TaskPriority.MEDIUM);
        model.setDeadline(LocalDate.now().plusDays(1));


        when(taskService.editTask(model)).thenThrow(new NotFound("Задача с id: " + id + " не найдена"));

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("provideTaskStatusChangeCases")
    void changeTaskStatus_API(
            TaskStatus initialStatus,
            LocalDate deadline,
            TaskStatus expectedStatus) throws Exception {

        UUID taskId = UUID.randomUUID();
        FullTaskModel task = new FullTaskModel();
        task.setStatus(initialStatus);
        task.setDeadline(deadline);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        doAnswer(invocation -> {
            task.setStatus(expectedStatus);
            return null;
        }).when(taskService).changeTaskStatus(taskId);
        mockMvc.perform(put("/api/task/" + taskId))
                .andExpect(status().isOk());
        verify(taskService, times(1)).changeTaskStatus(taskId);
        assertEquals(expectedStatus, task.getStatus());
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
    void changeTaskStatus_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NotFound("Задача не найдена")).when(taskService).changeTaskStatus(id);

        mockMvc.perform(put("/api/task/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_Success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseModel response = new ResponseModel("Success", "Task Deleted Successfully");

        when(taskService.deleteTask(id)).thenReturn(response);

        mockMvc.perform(delete("/api/task/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task Deleted Successfully"));
    }

    @Test
    void deleteTask_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(taskService.deleteTask(id)).thenThrow(new NotFound("Task not found"));

        mockMvc.perform(delete("/api/task/" + id))
                .andExpect(status().isNotFound());
    }
}
