package qa_lab.tasklistqalab.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qa_lab.tasklistqalab.dto.FullTaskModel;
import qa_lab.tasklistqalab.dto.ResponseModel;
import qa_lab.tasklistqalab.dto.ShortTaskModel;
import qa_lab.tasklistqalab.dto.TaskModel;
import qa_lab.tasklistqalab.service.TaskService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Task")
@RequiredArgsConstructor
@RequestMapping("/api/task")
@RestController
public class TaskController {

    private final TaskService taskService;


    @PostMapping()
    public ResponseEntity<UUID> createTask(@RequestBody TaskModel task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("{id}")
    public ResponseEntity<FullTaskModel> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ShortTaskModel>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping()
    public ResponseEntity<UUID> updateTask(@RequestBody TaskModel task) {
        return ResponseEntity.ok()
    }
}
