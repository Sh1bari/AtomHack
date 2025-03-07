package ru.noxly.simulation.services;

import lombok.*;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.redis.Reservoir;
import ru.noxly.simulation.repositories.redis.ReservoirRedisRepository;

@Service
@RequiredArgsConstructor
public class ReservoirRedisService {

	private final ReservoirRedisRepository repository;

	public Reservoir findById(Long id) {
		try {
			val reservoir = repository.findById(id);

			return reservoir.orElse(null);
		}catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
