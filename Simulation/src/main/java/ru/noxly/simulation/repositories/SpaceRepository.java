package ru.noxly.simulation.repositories;

import lombok.*;
import ru.noxly.simulation.models.entities.Space;
import ru.sh1bari.resolver.BaseJpaRepository;

public interface SpaceRepository extends BaseJpaRepository<Space, Long> {
}
