package ru.noxly.simulation.models.entities.redis;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("reservoirs")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class Reservoir implements Serializable {

	@Id
	private Long id;

	private Double pressure;
}
