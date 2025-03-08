package ru.noxly.simulation.repositories;


import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import ru.noxly.simulation.models.entities.Scheduler;
import ru.noxly.simulation.models.enums.SchedulerEnum;
import ru.sh1bari.resolver.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchedulerRepository extends BaseJpaRepository<Scheduler, SchedulerEnum> {
    List<Scheduler> findByEnabledTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "0") // немедленный таймаут
    })
    @Query("SELECT s FROM Scheduler s WHERE s.name = :name")
    Optional<Scheduler> findByNameForUpdate(SchedulerEnum name);
}