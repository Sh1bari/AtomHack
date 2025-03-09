package ru.noxly.simulation.services;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.redis.Reservoir;
import ru.noxly.simulation.repositories.redis.ReservoirRedisRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservoirRedisService {

	private final ReservoirRedisRepository repository;
	private boolean isRedisDown = false;

	public Reservoir findById(Long id) {
		if (isRedisDown) {
			return null;
		}

		try {
			Optional<Reservoir> reservoir = repository.findById(id);
			if (reservoir.isPresent()) {
				return reservoir.get();
			}
		} catch (Exception e) {
			log.error("Redis недоступен! Переключаюсь на БД. Ошибка: {}", e.getMessage());
			isRedisDown = true;
		}

		return null;
	}

	public void save(Reservoir reservoir) {
		if (isRedisDown) return;

		try {
			repository.save(reservoir);
		} catch (Exception e) {
			log.error("Ошибка записи в Redis, отключаем кеширование: {}", e.getMessage());
			isRedisDown = true;
		}
	}

	public void deleteById(Long id) {
		if (isRedisDown) return;

		try {
			repository.deleteById(id);
		} catch (Exception e) {
			log.error("Ошибка удаления из Redis: {}", e.getMessage());
			isRedisDown = true;
		}
	}

	@Scheduled(fixedDelay = 300000)
	public void checkRedisHealth() {
		if (!isRedisDown) return;

		try {
			repository.count();
			log.info("Redis снова доступен!");
			isRedisDown = false;
		} catch (Exception e) {
			log.warn("Redis все еще недоступен...");
		}
	}
}
