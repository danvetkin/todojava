package ToDoList.Application.Repositories.ModelsDTO.Task;

import ToDoList.Domain.Enums.TaskPriority;
import ToDoList.Domain.Enums.TaskStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class TaskShortModel {
    public TaskShortModel(){

    }

    public TaskShortModel(UUID id, String title, LocalDate deadline, TaskPriority priority, TaskStatus status,
                          Date createTime) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.createTime = createTime;
    }

    private UUID id;

    private String title;

    private TaskStatus status;

    private TaskPriority priority;

    @Nullable
    private LocalDate deadline;

    private Date createTime;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
