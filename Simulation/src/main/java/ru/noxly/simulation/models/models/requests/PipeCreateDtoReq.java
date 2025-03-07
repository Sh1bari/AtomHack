package ru.noxly.simulation.models.models.requests;

import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class PipeCreateDtoReq {

    private final Double diameter;

    private final Integer sourceId;

    private final Integer targetId;
}