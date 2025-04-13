package qa_lab.tasklistqalab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import qa_lab.tasklistqalab.entity.enum_model.TaskPriority;
import qa_lab.tasklistqalab.entity.enum_model.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "Tasks")
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private TaskStatus status;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'MEDIUM'")
    private TaskPriority priority;

    @Column(name = "update_date")
    @UpdateTimestamp
    private LocalDate updateDate;
}
