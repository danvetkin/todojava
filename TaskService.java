package ToDoList.Domain.Services;

import ToDoList.Application.Exceptions.CustomExceptions.KeyNotFoundException;
import ToDoList.Application.Exceptions.CustomExceptions.NotEnoughAccessException;
import ToDoList.Application.Repositories.ModelsDTO.Enums.TaskSortModel;
import ToDoList.Application.Repositories.ModelsDTO.Enums.UserTaskStatusModel;
import ToDoList.Application.Repositories.ModelsDTO.Task.*;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModelComparators.TaskShortModelAscCreateTimeComparator;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModelComparators.TaskShortModelAscDeadlineComparator;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModelComparators.TaskShortModelDescCreateTimeComparator;
import ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModelComparators.TaskShortModelDescDeadlineComparator;
import ToDoList.Application.Services.Interfaces.Task.ITaskService;
import ToDoList.Domain.Entities.Task.Task;
import ToDoList.Domain.Entities.User.User;
import ToDoList.Domain.Enums.TaskPriority;
import ToDoList.Domain.Enums.TaskStatus;
import ToDoList.Infrastructure.PostgreDB.Repositories.TaskRepository;
import ToDoList.Infrastructure.PostgreDB.Repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaskService implements ITaskService {

    private UserRepository _userRepository;
    private TaskRepository _taskRepository;

    // Внедрение зависимостей через конструктор
    public TaskService(UserRepository userRepository, TaskRepository taskRepository) {
        _taskRepository = taskRepository;
        _userRepository = userRepository;
    }

    // Метод создания новой задачи
    public UUID createTask(UUID userId, TaskCreateModel taskCreateModel) throws KeyNotFoundException, BadRequestException {
        Optional<User> userO = _userRepository.findById(userId);

        // Проверка наличия пользователя
        if (!userO.isPresent()) {
            throw new KeyNotFoundException("User is not found");
        }

        TaskPriority taskPriority = taskCreateModel.getPriority();
        LocalDate deadline = taskCreateModel.getDeadline();

        // Автоматическое определение приоритета по макросу в названии задачи
        if (taskCreateModel.getPriority() == null) {
            String[] titleRef = new String[]{taskCreateModel.getTitle()};
            taskPriority = calculateTaskPriorityByTitleMacros(titleRef, TaskPriority.Medium);
            taskCreateModel.setTitle(titleRef[0]); // Обновление заголовка без макроса
        }

        // Автоматическое определение дедлайна по макросу в названии задачи
        if (taskCreateModel.getDeadline() == null) {
            String[] titleRef = new String[]{taskCreateModel.getTitle()};
            deadline = calculateTaskDeadlineByTitleMacros(titleRef);
            taskCreateModel.setTitle(titleRef[0]); // Обновление заголовка без макроса
        }

        // Создание объекта задачи и его сохранение
        Task task = new Task(
                UUID.randomUUID(),
                userO.get().getId(),
                taskCreateModel.getTitle(),
                taskCreateModel.getDescription(),
                TaskStatus.Active,
                taskPriority,
                deadline,
                Calendar.getInstance().getTime(),
                null
        );

        _taskRepository.save(task);
        return task.getId();
    }

    // Определение приоритета задачи по макросу (!1, !2 и т.д.)
    private TaskPriority calculateTaskPriorityByTitleMacros(String[] titleRef, TaskPriority defaultPriority) {
        String title = titleRef[0];
        Pattern pattern = Pattern.compile("![1-4]");
        Matcher matcher = pattern.matcher(title);

        TaskPriority taskPriority = defaultPriority;

        if (matcher.find()) {
            String findedResult = title.substring(matcher.start(), matcher.end());
            titleRef[0] = title.replace(findedResult, ""); // Удаление макроса из заголовка
            switch (findedResult) {
                case "!1": taskPriority = TaskPriority.Low; break;
                case "!2": taskPriority = TaskPriority.Medium; break;
                case "!3": taskPriority = TaskPriority.High; break;
                case "!4": taskPriority = TaskPriority.Critical; break;
            }
        }
        return taskPriority;
    }

    // Определение дедлайна по макросу "!before dd.mm.yyyy" или "!before dd-mm-yyyy"
    private LocalDate calculateTaskDeadlineByTitleMacros(String[] titleRef) {
        String title = titleRef[0];
        Pattern pattern = Pattern.compile("!before ((0[1-9]|1[0-9]|2[0-9]|3[0-1])[.-](0[1-9]|1[012])[.-][0-9]{4})");
        Matcher matcher = pattern.matcher(title);

        LocalDate deadline = null;

        if (matcher.find()) {
            String findedResult = title.substring(matcher.start(), matcher.end());
            titleRef[0] = title.replace(findedResult, ""); // Удаление макроса из заголовка

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dateGroup = matcher.group(1).replace(".", "-"); // Приведение к формату dd-MM-yyyy

            deadline = LocalDate.parse(dateGroup, inputFormatter);
        }
        return deadline;
    }

    // Метод редактирования задачи
    public void editTask(UUID taskId, UUID userId, EditTaskModel editTaskModel)
            throws KeyNotFoundException, BadRequestException, NotEnoughAccessException {
        Optional<User> userO = _userRepository.findById(userId);
        Optional<Task> taskO = _taskRepository.findById(taskId);

        // Проверки наличия задачи и пользователя
        if (!taskO.isPresent()) {
            throw new KeyNotFoundException("Task is not found");
        }
        if (!userO.isPresent()) {
            throw new KeyNotFoundException("User is not found");
        }

        Task task = taskO.get();

        // Вычисление приоритета и дедлайна, если не указаны явно
        TaskPriority taskPriority = editTaskModel.getPriority();
        LocalDate deadline = editTaskModel.getDeadline();

        if (editTaskModel.getPriority() == null) {
            String[] titleRef = new String[]{editTaskModel.getTitle()};
            taskPriority = calculateTaskPriorityByTitleMacros(titleRef, TaskPriority.Medium);
            editTaskModel.setTitle(titleRef[0]);
        }

        if (editTaskModel.getDeadline() == null) {
            String[] titleRef = new String[]{editTaskModel.getTitle()};
            deadline = calculateTaskDeadlineByTitleMacros(titleRef);
            editTaskModel.setTitle(titleRef[0]);
        }

        // Проверка прав доступа
        if (!task.getUserId().equals(userId)) {
            throw new NotEnoughAccessException("You are trying to get not your task");
        }

        // Обновление полей задачи
        task.setTitle(editTaskModel.getTitle());
        task.setDescription(editTaskModel.getDescription());
        task.setDeadline(deadline);
        task.setPriority(taskPriority);
        task.setUpdateTime(Calendar.getInstance().getTime());

        // Автоматическая смена статуса в зависимости от дедлайна
        if (editTaskModel.getStatus().equals(TaskStatus.Active) || editTaskModel.getStatus().equals(TaskStatus.Overdue)) {
            task.setStatus((deadline != null && deadline.isBefore(LocalDate.now())) ? TaskStatus.Overdue : TaskStatus.Active);
        } else {
            task.setStatus((deadline != null && deadline.isBefore(LocalDate.now())) ? TaskStatus.Late : TaskStatus.Completed);
        }

        _taskRepository.save(task);
    }

    // Метод удаления задачи
    public void deleteTask(UUID taskId, UUID userId) throws KeyNotFoundException, NotEnoughAccessException {
        Optional<Task> taskO = _taskRepository.findById(taskId);
        Optional<User> userO = _userRepository.findById(userId);

        if (!taskO.isPresent()) {
            throw new KeyNotFoundException("Task is not found");
        }
        if (!userO.isPresent()) {
            throw new KeyNotFoundException("User is not found");
        }

        Task task = taskO.get();

        // Проверка владельца задачи
        if (!task.getUserId().equals(userId)) {
            throw new NotEnoughAccessException("You are trying to get not your task");
        }

        _taskRepository.deleteById(taskId);
    }

    // Метод обновления статуса задачи, если дедлайн просрочен
    private void changeTaskStatus(Task task) {
        TaskStatus status = task.getStatus();
        LocalDate deadline = task.getDeadline();

        if (deadline != null && status == TaskStatus.Active && deadline.isBefore(LocalDate.now())) {
            task.setStatus(TaskStatus.Overdue);
        }
    }

    // Получение полной информации о задаче
    public TaskModel getTask(UUID taskId, UUID userId) throws KeyNotFoundException, NotEnoughAccessException {
        Optional<Task> taskO = _taskRepository.findById(taskId);
        Optional<User> userO = _userRepository.findById(userId);

        if (!taskO.isPresent()) {
            throw new KeyNotFoundException("Task is not found");
        }
        if (!userO.isPresent()) {
            throw new KeyNotFoundException("User is not found");
        }

        Task task = taskO.get();

        if (!task.getUserId().equals(userId)) {
            throw new NotEnoughAccessException("You are trying to get not your task");
        }

        // Обновление статуса, если нужно
        changeTaskStatus(task);
        _taskRepository.save(task);

        return new TaskModel(task.getId(),
                task.getTitle(),
                task.getDeadline(),
                task.getPriority(),
                task.getStatus(),
                task.getDescription(),
                task.getCreateTime(),
                task.getUpdateTime()
        );
    }

    // Получение списка задач пользователя с сортировкой
    public TaskShortModelList getUserTasks(UUID userId, TaskSortModel taskSortModel) throws KeyNotFoundException {
        Optional<User> userO = _userRepository.findById(userId);

        if (!userO.isPresent()) {
            throw new KeyNotFoundException("User is not found");
        }

        List<Task> taskList = _taskRepository.findByUserId(userId);
        List<TaskShortModel> taskShortModels = new ArrayList<>();

        // Преобразование задач в упрощенные модели
        taskList.forEach(task -> taskShortModels.add(
                new TaskShortModel(task.getId(),
                        task.getTitle(),
                        task.getDeadline(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getCreateTime())
        ));

        // Сортировка списка задач, если задана модель сортировки
        if (taskSortModel != null) {
            Comparator comparator;
            switch (taskSortModel) {
                case AscCreationTime:
                    comparator = new TaskShortModelAscCreateTimeComparator(); break;
                case DescCreationTime:
                    comparator = new TaskShortModelDescCreateTimeComparator(); break;
                case DescDeadline:
                    comparator = new TaskShortModelDescDeadlineComparator(); break;
                case AscDeadline:
                    comparator = new TaskShortModelAscDeadlineComparator(); break;
                default:
                    comparator = new TaskShortModelDescCreateTimeComparator();
            }
            taskShortModels.sort(comparator);
        }

        return new TaskShortModelList(taskShortModels);
    }
}
