package qa_lab.tasklistqalab.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskListModel {
    List<ShortTaskModel> taskList;
}
