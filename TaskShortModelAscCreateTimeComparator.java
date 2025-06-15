package ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModelComparators;

import ToDoList.Application.Repositories.ModelsDTO.Task.TaskShortModel;

import java.util.Comparator;
import java.util.Date;

public class TaskShortModelAscCreateTimeComparator implements Comparator<TaskShortModel> {
    @Override
    public int compare(TaskShortModel o1, TaskShortModel o2) {
        Date fCreateTime = o1.getCreateTime();
        Date sCreateTime = o2.getCreateTime();

        if (fCreateTime.after(sCreateTime)) {
            return 1;
        }else if(fCreateTime.before(sCreateTime)) {
            return -1;
        }else{
            return 0;
        }
    }
}
