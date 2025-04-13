package qa_lab.tasklistqalab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qa_lab.tasklistqalab.dto.*;
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
    public ResponseEntity<UUID> createTask(@RequestBody TaskModel task) {
        return ok(taskService.createTask(task));
    }

    @GetMapping("{id}")
    public ResponseEntity<FullTaskModel> getTaskById(@PathVariable UUID id) {
        return ok(taskService.getTaskById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ShortTaskModel>> getAllTasks() {
        return ok(taskService.getAllTasks());
    }

    @PutMapping()
    public ResponseEntity<ResponseModel> updateTask(@RequestBody EditTaskModel task) {
        return ok(taskService.editTask(task));
    }

    @PutMapping("{id}")
    public Void changeTaskStatus(@PathVariable UUID id) {
        return taskService.changeTaskStatus(id);
    }
}
