package ru.noxly.simulation.webClients.dyndns.models;

import lombok.*;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
public class DyndnsResponse {

    private final String result;

    private final String id;

    private final Double pressure;

    private final String unit;
}