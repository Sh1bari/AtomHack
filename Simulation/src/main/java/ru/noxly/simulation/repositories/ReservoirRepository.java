package ru.noxly.simulation.repositories;

import lombok.*;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.sh1bari.resolver.BaseJpaRepository;

public interface ReservoirRepository extends BaseJpaRepository<Reservoir, Long> {
}
