package qa_lab.tasklistqalab.entity.enum_model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Направление сортировки", enumAsRef = true)
public enum SortDirection {
    ASC, DESC
}