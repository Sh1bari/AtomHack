package ru.noxly.simulation.repositories;

import lombok.*;
import ru.noxly.simulation.models.entities.Pipe;
import ru.sh1bari.resolver.BaseJpaRepository;

public interface PipeRepository extends BaseJpaRepository<Pipe, Long> {
}
