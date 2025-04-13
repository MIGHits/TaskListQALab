package qa_lab.tasklistqalab.specification;

import org.springframework.data.jpa.domain.Specification;
import qa_lab.tasklistqalab.entity.TaskEntity;
import qa_lab.tasklistqalab.entity.enum_model.SortDirection;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

public class TaskSpecification {
    public static Specification<TaskEntity> sortByPriority(SortDirection sortDirection) {
        return ((root, query, criteriaBuilder) -> {
            if (sortDirection == SortDirection.DESC) {
                query.orderBy(criteriaBuilder.desc(root.get("priority")));
            } else {
                query.orderBy(criteriaBuilder.asc(root.get("priority")));
            }
            return null;
        });
    }

    public static Specification<TaskEntity> sortByCreateTime(SortDirection sortDirection) {
        return ((root, query, criteriaBuilder) -> {
            if (sortDirection == SortDirection.ASC) {
                query.orderBy(criteriaBuilder.asc(root.get("creationDate")));
            } else {
                query.orderBy(criteriaBuilder.desc(root.get("creationDate")));
            }
            return null;
        });
    }

    public static Specification<TaskEntity> filterByStatus(TaskStatus status) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("status"),status);
    }
}
