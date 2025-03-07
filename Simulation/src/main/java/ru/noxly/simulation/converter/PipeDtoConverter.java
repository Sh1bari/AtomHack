package ru.noxly.simulation.converter;

import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Pipe;
import ru.noxly.simulation.models.models.dtos.PipeDto;

@Component
public class PipeDtoConverter implements Converter<Pipe, PipeDto> {
	@Override
	public PipeDto convert(@NonNull final Pipe source) {
		return null;
	}
}
