package ru.noxly.simulation.repositories.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.noxly.simulation.models.entities.redis.Reservoir;

@Repository
public interface ReservoirRedisRepository extends CrudRepository<Reservoir, Long> {
}
