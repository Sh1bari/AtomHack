package ru.noxly.simulation.webClients.dyndns;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.webClients.dyndns.models.DyndnsResponse;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynDnsClient {

    private final DyndnsWebClient dyndnsWebClient;

    public Double getPressure(Long id) {
        val uri = format("/api/hackathon.aspx?id=%s", id);
        val response = dyndnsWebClient.get(
                uri,
                DyndnsResponse.class
        );

        return response.getPressure();
    }
}