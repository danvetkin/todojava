

// Импорты необходимых зависимостей
import ToDoList.Domain.Enums.TaskPriority;
import ToDoList.Domain.Enums.TaskStatus;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
 * Полная модель задачи, содержащая все основные атрибуты
 * Используется для передачи данных между слоями приложения
 */
public class TaskModel {
    
    // Конструктор по умолчанию 
    public TaskModel() {
    }

    // Полный конструктор для инициализации всех полей модели
    public TaskModel(UUID id, String title, LocalDate deadline, TaskPriority priority, 
                   TaskStatus status, String description, Date createTime, Date updateTime) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Уникальный идентификатор задачи
    private UUID id;

    // Название задачи (обязательное поле)
    private String title;

    // Подробное описание задачи
    private String description;

    // Текущий статус выполнения задачи
    private TaskStatus status;

    // Уровень приоритета задачи
    private TaskPriority priority;

    // Планируемая дата выполнения
    private LocalDate deadline;

    // Дата и время создания записи о задаче
    private Date createTime;

    // Дата и время последнего обновления задачи
    private Date updateTime;

    // Методы доступа к полям модели
    
    /**
     * @return уникальный идентификатор задачи
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Устанавливает идентификатор задачи
     * @param id - UUID задачи
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return текущее название задачи
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Устанавливает новое название задачи
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Устанавливает новое описание задачи
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     */
    public TaskStatus getStatus() {
        return status;
    }
    
    /**
     * Устанавливает новый статус задачи
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     */
    public TaskPriority getPriority() {
        return priority;
    }
    
    /**
     * Устанавливает приоритет задачи
     */
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     */
    public LocalDate getDeadline() {
        return deadline;
    }
    
    /**
     * Устанавливает срок выполнения задачи
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     */
    public Date getCreateTime() {
        return createTime;
    }
    
    /**
     * Устанавливает дату создания задачи
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     */
    public Date getUpdateTime() {
        return updateTime;
    }
    
    /**
     * Устанавливает дату последнего обновления
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}