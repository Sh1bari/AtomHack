package ru.noxly.simulation.converter;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.SpaceDto;
import ru.noxly.simulation.utils.Formatter;

import static ru.noxly.simulation.utils.CommonUtils.nullOrApply;

@Component
public class SpaceDtoConverter implements Converter<Space, SpaceDto> {
	@Override
	public SpaceDto convert(@NonNull final Space source) {
		return SpaceDto.init()
				.setId(source.getId())
				.setName(source.getName())
				.setCreateDate(nullOrApply(source.getCreateDate(), Formatter.formatter::format))
				.build();
	}
}
