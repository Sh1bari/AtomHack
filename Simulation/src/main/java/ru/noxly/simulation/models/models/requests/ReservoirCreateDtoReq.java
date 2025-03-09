package ru.noxly.simulation.models.models.requests;

import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class ReservoirCreateDtoReq {

    private final Double area;

    private final Double startLevel;

    private final Long spaceId;
}