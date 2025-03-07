package ru.noxly.simulation.converter;

import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;

@Component
public class ReservoirDtoConverter implements Converter<Reservoir, ReservoirDto> {
	@Override
	public ReservoirDto convert(@NonNull final Reservoir source) {
		return ReservoirDto.init()
				.setId(source.getId())
				.setArea(source.getArea())
				.setLevel(source.getLevel())
				.setPressure(source.getPressure())
				.setSpaceId(source.getSpace().getId())
				.build();
	}
}
