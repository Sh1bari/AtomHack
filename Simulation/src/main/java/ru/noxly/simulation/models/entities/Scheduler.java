package ru.noxly.simulation.models.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.noxly.simulation.models.enums.SchedulerEnum;
import ru.noxly.simulation.models.enums.SchedulerStatus;

import java.time.OffsetDateTime;

@Entity
@Setter
@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Table(name = "schedulers")
public class Scheduler {

    @Id
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private SchedulerEnum name;

    @Enumerated(EnumType.STRING)
    private SchedulerType type;

    private Boolean enabled;

    private String cronExpression;

    private Long fixedDelay;

    @Basic
    private OffsetDateTime startTime;

    @Enumerated(EnumType.STRING)
    private SchedulerStatus status;

    @Basic
    private OffsetDateTime lastRunAt;

    public enum SchedulerType {
        ONE_TIME,       // Одноразовый запуск
        FIXED_DELAY,    // Периодическая задача с задержкой
        CRON            // Задача по cron-выражению
    }

}