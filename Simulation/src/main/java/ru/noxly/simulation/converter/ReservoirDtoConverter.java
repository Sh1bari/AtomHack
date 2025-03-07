package ru.noxly.simulation.converter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;
import ru.noxly.simulation.services.ReservoirRedisService;

import static ru.noxly.simulation.utils.CommonUtils.nullOrApply;
import static ru.noxly.simulation.utils.CommonUtils.nullOrDefault;
import static ru.noxly.simulation.utils.ReservoirIdUtil.resolveReservoirId;

@Component
@RequiredArgsConstructor
public class ReservoirDtoConverter implements Converter<Reservoir, ReservoirDto> {

	private final ReservoirRedisService reservoirRedisService;

	@Override
	public ReservoirDto convert(@NonNull final Reservoir source) {
		return ReservoirDto.init()
				.setId(source.getId())
				.setArea(source.getArea())
				.setLevel(source.getLevel())
				.setPressure(
						nullOrDefault(
								nullOrApply(reservoirRedisService.findById(resolveReservoirId(source.getId())),
										ru.noxly.simulation.models.entities.redis.Reservoir::getPressure),
								source.getPressure()
						)
				)
				.setSpaceId(source.getSpace().getId())
				.build();
	}
}
