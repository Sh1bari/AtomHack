package ru.noxly.simulation.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.sh1bari.resolver.BaseJpaRepository;

import java.util.List;

public interface ReservoirRepository extends BaseJpaRepository<Reservoir, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE reservoirs r
        SET pressure = s.pressure
        FROM (
            SELECT UNNEST(CAST(:keys AS bigint[])) AS key,
                   UNNEST(CAST(:pressures AS double precision[])) AS pressure
        ) AS s
        WHERE (r.id % 10 + 1) = s.key;
        """, nativeQuery = true)
    void updatePressure(@Param("keys") Long[] keys, @Param("pressures") Double[] pressures);



}
