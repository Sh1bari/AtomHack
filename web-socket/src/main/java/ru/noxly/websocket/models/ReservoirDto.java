package ru.noxly.websocket.models;
import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class ReservoirDto {

	private final Long id;

	private final Long spaceId;

	private final Double pressure;

	private final Double level;

	private final Double area;
}
