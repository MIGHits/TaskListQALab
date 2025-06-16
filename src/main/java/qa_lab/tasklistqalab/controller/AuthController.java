package qa_lab.tasklistqalab.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qa_lab.tasklistqalab.dto.ResponseModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    // Имитация хранилища пользователей
    private static final Map<String, String> userCredentials = new HashMap<>();
    private static final Map<String, String> userSessions = new HashMap<>();
    
    static {
        userCredentials.put("admin", "admin123");
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseModel> login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request) {
        
        // Уязвимый код - небезопасная обработка сессии
        String requestedSessionId = request.getRequestedSessionId();
        
        // Проверяем учетные данные
        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            // Используем sessionId, предоставленный клиентом, если он есть
            String sessionId = requestedSessionId != null ? requestedSessionId : UUID.randomUUID().toString();
            
            // Сохраняем сессию
            userSessions.put(sessionId, username);
            
            // Устанавливаем сессию в ответ
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", username);
            session.setAttribute("sessionId", sessionId);
            
            return ResponseEntity.ok(ResponseModel.builder()
                    .status("success")
                    .message("Успешная аутентификация")
                    .build());
        }
        
        return ResponseEntity.badRequest().body(ResponseModel.builder()
                .status("error")
                .message("Неверные учетные данные")
                .build());
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseModel> checkSession(HttpServletRequest request) {
        String requestedSessionId = request.getRequestedSessionId();
        
        // Уязвимый код - доверяем sessionId от клиента
        if (requestedSessionId != null && userSessions.containsKey(requestedSessionId)) {
            return ResponseEntity.ok(ResponseModel.builder()
                    .status("success")
                    .message("Сессия активна для пользователя: " + userSessions.get(requestedSessionId))
                    .build());
        }
        
        return ResponseEntity.badRequest().body(ResponseModel.builder()
                .status("error")
                .message("Недействительная сессия")
                .build());
    }
} 