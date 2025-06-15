package ToDoList.Application.Repositories.ModelsDTO.Task;

import ToDoList.Domain.Enums.TaskPriority;
import ToDoList.Domain.Enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class EditTaskModel {
    public EditTaskModel(){

    }

    public EditTaskModel(String title, LocalDate deadline, TaskPriority priority, TaskStatus status, String description) {
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.description = description;
        this.status = status;
    }

    private String title;

    private String description;

    @Nullable
    private TaskPriority priority;

    private TaskStatus status;

    @Nullable
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate deadline;


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}
