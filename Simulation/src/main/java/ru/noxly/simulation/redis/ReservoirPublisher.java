package ru.noxly.simulation.redis;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.models.socket.ReservoirUpdateSocketDto;

@Service
@RequiredArgsConstructor
public class ReservoirPublisher {

	@Autowired
	@Qualifier("pubSubRedisTemplate")
	private RedisTemplate<String, ReservoirUpdateSocketDto> redisTemplate;

	public void publishUpdate(ReservoirUpdateSocketDto reservoirDto) {
		val spaceId = reservoirDto.getSpaceId();
		if (spaceId == null) {
			throw new IllegalArgumentException("SpaceId не может быть null");
		}

		val channel = "reservoir-updates:" + spaceId;
		redisTemplate.convertAndSend(channel, reservoirDto);
	}
}
