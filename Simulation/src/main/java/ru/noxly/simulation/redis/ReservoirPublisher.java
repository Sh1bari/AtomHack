package ru.noxly.simulation.redis;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;

@Service
@RequiredArgsConstructor
public class ReservoirPublisher {

	@Autowired
	@Qualifier("pubSubRedisTemplate")
	private RedisTemplate<String, ReservoirDto> redisTemplate;

	public void publishUpdate(ReservoirDto reservoirDto) {
		if (reservoirDto.getSpaceId() == null) {
			throw new IllegalArgumentException("SpaceId не может быть null");
		}

		String channel = "reservoir-updates:" + reservoirDto.getSpaceId();
		redisTemplate.convertAndSend(channel, reservoirDto);
	}
}
