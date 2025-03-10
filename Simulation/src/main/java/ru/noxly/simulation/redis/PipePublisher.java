package ru.noxly.simulation.redis;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.models.socket.PipeUpdateSocketDto;
import ru.noxly.simulation.models.models.socket.ReservoirUpdateSocketDto;

@Service
@RequiredArgsConstructor
public class PipePublisher {

    @Autowired
    @Qualifier("pubSubPipeRedisTemplate")
    private RedisTemplate<String, PipeUpdateSocketDto> redisTemplate;

    public void publishUpdate(Long spaceId, PipeUpdateSocketDto pipeDto) {
        if (spaceId == null) {
            throw new IllegalArgumentException("SpaceId не может быть null");
        }

        val channel = "pipe-updates:" + spaceId;
        redisTemplate.convertAndSend(channel, pipeDto);
    }
}
