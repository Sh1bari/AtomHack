package ru.noxly.simulation.converter;

import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.SpaceByIdDto;

@Component
public class SpaceByIdDtoConverter implements Converter<Space, SpaceByIdDto> {
	@Override
	public SpaceByIdDto convert(@NonNull final Space source) {
		return null;
	}
}
