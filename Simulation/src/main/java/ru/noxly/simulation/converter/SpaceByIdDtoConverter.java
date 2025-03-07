package ru.noxly.simulation.converter;

import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.SpaceByIdDto;

@Component
@RequiredArgsConstructor
public class SpaceByIdDtoConverter implements Converter<Space, SpaceByIdDto> {

	private final SpaceDtoConverter spaceDtoConverter;

	@Override
	public SpaceByIdDto convert(@NonNull final Space source) {
		return SpaceByIdDto.init()
				.setSpace(spaceDtoConverter.convert(source))
				.build();
	}
}
