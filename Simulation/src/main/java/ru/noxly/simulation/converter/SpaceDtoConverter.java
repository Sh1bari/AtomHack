package ru.noxly.simulation.converter;

import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.SpaceDto;

@Component
public class SpaceDtoConverter implements Converter<Space, SpaceDto> {
	@Override
	public SpaceDto convert(@NonNull final Space source) {
		return null;
	}
}
