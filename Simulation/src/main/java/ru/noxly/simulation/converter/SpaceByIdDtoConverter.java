package ru.noxly.simulation.converter;

import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.dtos.SpaceByIdDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpaceByIdDtoConverter implements Converter<Space, SpaceByIdDto> {

	private final SpaceDtoConverter spaceDtoConverter;
	private final ReservoirDtoConverter reservoirDtoConverter;
	private final PipeDtoConverter pipeDtoConverter;

	@Override
	public SpaceByIdDto convert(@NonNull final Space source) {
		return SpaceByIdDto.init()
				.setSpace(spaceDtoConverter.convert(source))
				.setReservoirs(source.getReservoirs()
						.stream()
						.map(reservoirDtoConverter::convert)
						.toList())
				.setPipes(source.getReservoirs()
						.stream()
						.map(Reservoir::getOutgoingPipes) // Получаем List<Pipe>
						.flatMap(List::stream) // Преобразуем List<Pipe> в Stream<Pipe>
						.map(pipeDtoConverter::convert) // Конвертируем Pipe в PipeDto
						.toList())
				.build();
	}
}
