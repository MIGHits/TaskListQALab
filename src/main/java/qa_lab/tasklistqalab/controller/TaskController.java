package qa_lab.tasklistqalab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qa_lab.tasklistqalab.dto.*;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.SortField;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;
import qa_lab.tasklistqalab.service.TaskService;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Task")
@RequiredArgsConstructor
@RequestMapping("/api/task")
@RestController
public class TaskController {

    private final TaskService taskService;


    @PostMapping()
    public ResponseEntity<UUID> createTask(@RequestBody @Valid TaskModel task) {
        return ok(taskService.createTask(task));
    }

    @GetMapping("{id}")
    public ResponseEntity<FullTaskModel> getTaskById(@PathVariable UUID id) {
        return ok(taskService.getTaskById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ShortTaskModel>> getAllTasks(
            @RequestParam(required = false, defaultValue = "ASC") SortDirection sortDirection,
            @RequestParam(required = false, defaultValue = "CREATION_DATE") SortField sortField,
            @RequestParam(required = false) TaskStatus status) {
        return ok(taskService.getAllTasks(sortField, status, sortDirection));
    }

    @PutMapping()
    public ResponseEntity<ResponseModel> updateTask(@RequestBody @Valid EditTaskModel task) {
        return ok(taskService.editTask(task));
    }

    @PutMapping("{id}")
    public Void changeTaskStatus(@PathVariable UUID id) {
        return taskService.changeTaskStatus(id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseModel> deleteTask(@PathVariable UUID id) {
        return ok(taskService.deleteTask(id));
    }
}
