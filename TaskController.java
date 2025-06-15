package ToDoList.Presentation.Controllers;

import ToDoList.Application.Exceptions.CustomExceptions.KeyNotFoundException;
import ToDoList.Application.Exceptions.CustomExceptions.NotEnoughAccessException;
import ToDoList.Application.Repositories.ModelsDTO.Enums.TaskSortModel;
import ToDoList.Application.Repositories.ModelsDTO.Task.EditTaskModel;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskCreateModel;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskModel;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModelList;
import ToDoList.Application.Services.Interfaces.Task.ITaskService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController // Помечает класс как контроллер REST, обрабатывающий HTTP-запросы
@RequestMapping("/api/v1/tasks") // Базовый маршрут для всех endpoints данного контроллера
public class TaskController {

    private ITaskService _taskService;

    // Конструктор с внедрением зависимости — сервис задач
    public TaskController(ITaskService taskService) {
        _taskService = taskService;
    }

    /**
     * Создание новой задачи
     * param taskCreateModel модель данных для создания задачи
     * return UUID созданной задачи
     */
    @PostMapping
    public ResponseEntity<UUID> CreateTask(@RequestBody TaskCreateModel taskCreateModel)
            throws BadRequestException, KeyNotFoundException {

        // Получение ID пользователя из контекста безопасности
        UUID userId  = GetUserIdFromSecurityContext();

        // Создание задачи и возврат её UUID
        return ResponseEntity.ok(_taskService.createTask(userId, taskCreateModel));
    }

    /**
     * Редактирование существующей задачи
     * param editTaskModel модель редактирования
     * param taskId идентификатор задачи
     * return HTTP 200 OK, если задача успешно отредактирована
     */
    @PutMapping("{taskId}")
    @ResponseStatus(code = HttpStatus.OK, reason = "OK")
    public ResponseEntity<?> EditTask(@RequestBody EditTaskModel editTaskModel, @PathVariable("taskId") UUID taskId)
            throws BadRequestException, KeyNotFoundException, NotEnoughAccessException {

        UUID userId  = GetUserIdFromSecurityContext();

        // Редактирование задачи
        _taskService.editTask(taskId, userId, editTaskModel);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление задачи
     * param taskId идентификатор удаляемой задачи
     * return HTTP 200 OK
     */
    @DeleteMapping("{taskId}")
    @ResponseStatus(code = HttpStatus.OK, reason = "OK")
    public ResponseEntity<?> DeleteTask(@PathVariable("taskId") UUID taskId)
            throws BadRequestException, KeyNotFoundException, NotEnoughAccessException {

        UUID userId  = GetUserIdFromSecurityContext();

        // Удаление задачи
        _taskService.deleteTask(taskId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение полной информации о задаче
     * param taskId идентификатор задачи
     * return объект TaskModel с деталями задачи
     */
    @GetMapping("{taskId}")
    public ResponseEntity<TaskModel> GetTask(@PathVariable("taskId") UUID taskId)
            throws BadRequestException, KeyNotFoundException, NotEnoughAccessException {

        UUID userId  = GetUserIdFromSecurityContext();

        // Получение задачи
        return ResponseEntity.ok(_taskService.getTask(taskId, userId));
    }

    /**
     * Получение всех задач пользователя, с возможной сортировкой
     * param taskSortModel тип сортировки (опционально)
     * return список кратких моделей задач
     */
    @GetMapping
    public ResponseEntity<TaskShortModelList> GetUserTasks(@RequestParam(name = "taskSort", required = false) TaskSortModel taskSortModel)
            throws BadRequestException, KeyNotFoundException {

        UUID userId  = GetUserIdFromSecurityContext();

        // Получение всех задач пользователя с учётом сортировки
        return ResponseEntity.ok(_taskService.getUserTasks(userId, taskSortModel));
    }

    /**
     * Вспомогательный метод для извлечения ID текущего пользователя
     * из Spring SecurityContext
     */
    private UUID GetUserIdFromSecurityContext() {
        return UUID.fromString((String)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal());
    }
}
