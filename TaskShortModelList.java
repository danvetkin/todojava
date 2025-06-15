package ToDoList.Application.Repositories.ModelsDTO.Task;

import ToDoList.Domain.Enums.TaskPriority;
import ToDoList.Domain.Enums.TaskStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskShortModelList {
    public TaskShortModelList(){

    }

    public TaskShortModelList(List<TaskShortModel> taskShortModelList) {
        this.taskShortModelList = taskShortModelList;
    }

    private List<TaskShortModel> taskShortModelList;

    public List<TaskShortModel> getTaskShortModelList() {
        return taskShortModelList;
    }
    public void setTaskShortModelList(List<TaskShortModel> taskShortModelList) {
        this.taskShortModelList = taskShortModelList;
    }

}
